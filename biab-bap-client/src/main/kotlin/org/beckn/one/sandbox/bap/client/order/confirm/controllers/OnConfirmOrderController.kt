package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.confirm.services.OnConfirmOrderService
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientConfirmResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.mappers.OnOrderProtocolToEntityOrder
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnConfirm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.profiles.ProfileFile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URL
import java.nio.file.Paths

@RestController
class OnConfirmOrderController @Autowired constructor(
  onPollService: GenericOnPollService<ProtocolOnConfirm, ClientConfirmResponse>,
  val contextFactory: ContextFactory,
  val protocolClient: ProtocolClient,
  val mapping: OnOrderProtocolToEntityOrder,
  val onConfirmOrderService: OnConfirmOrderService,
  val loggingFactory: LoggingFactory,
  val loggingService: LoggingService,
) : AbstractOnPollController<ProtocolOnConfirm, ClientConfirmResponse>(onPollService, contextFactory, loggingFactory, loggingService) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/on_confirm_order")
  @ResponseBody
  fun onConfirmOrderV1(
    @RequestParam messageId: String
  ): ResponseEntity<out ClientResponse> = onPoll(
    messageId,
    protocolClient.getConfirmResponsesCall(messageId),
    ProtocolContext.Action.ON_CONFIRM
  )

  @RequestMapping("/client/v2/on_confirm_order")
  @ResponseBody
  fun onConfirmOrderV2(
    @RequestParam messageIds: String
  ): ResponseEntity<out List<ClientResponse>> {
    //val user = SecurityUtil.getSecuredUserDetail()
    //if (user != null) {
      if (messageIds.isNotEmpty() && messageIds.trim().isNotEmpty()) {
        val messageIdArray = messageIds.split(",")
        var okResponseConfirmOrder: MutableList<ClientConfirmResponse> = ArrayList()
          for (messageId in messageIdArray) {
            val contextProtocol = contextFactory.create(messageId = messageId)
            val bapResult = onPoll(
              messageId,
              protocolClient.getConfirmResponsesCall(messageId),
              ProtocolContext.Action.ON_CONFIRM
            )
            when (bapResult.statusCode.value()) {
              200 -> {
                val resultResponse: ClientConfirmResponse = bapResult.body as ClientConfirmResponse
                if (resultResponse.message?.order != null) {
                  onConfirmOrderService.findById(resultResponse.context?.messageId).fold(
                    {
                      log.error("Db error to fetch order based on message id")
                      setLogging(resultResponse.context!!, it)
                      okResponseConfirmOrder.add(
                        ClientConfirmResponse(
                          error = it.error(),
                          context =contextProtocol
                        )
                      )
                    },{
                      val orderDao: OrderDao = mapping.protocolToEntity(resultResponse.message.order!!)
                      orderDao.transactionId = resultResponse.context?.transactionId
                      //orderDao.userId = user.uid
                      orderDao.messageId = resultResponse.context?.messageId
                      orderDao.parentOrderId = it.parentOrderId
                      onConfirmOrderService.updateOrder(orderDao).fold(
                        {
                          setLogging(resultResponse.context!!, it)
                          okResponseConfirmOrder.add(
                            ClientConfirmResponse(
                              error = it.error(),
                              context = contextFactory.create(messageId = messageId)
                            )
                          )
                        }, {
                          resultResponse.parentOrderId = orderDao.parentOrderId
                          val credentialsFile = Paths.get(System.getProperty("user.dir")+"\\src\\main\\resources\\credentials")
                          val credentialsProvider: AwsCredentialsProvider = ProfileCredentialsProvider.builder()
                            .profileName("dev")
                            .profileFile(ProfileFile.builder().content(credentialsFile).type(ProfileFile.Type.CREDENTIALS).build())
                            .build()


                          val s3Client = S3Client.builder()
                            .region(Region.AP_SOUTH_1)
                            .credentialsProvider(credentialsProvider)
                            .build()
                          val req = PutObjectRequest.builder().bucket("s3-order-json-test").key(orderDao.transactionId).build()
                          s3Client.putObject(req, RequestBody.fromString(resultResponse.message.order.toString()))
                          val url: URL = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket("s3-order-json-test").key(orderDao.transactionId).build())
                          resultResponse.order_url= url.toString()
                          okResponseConfirmOrder.add(resultResponse)
                        }
                      )

                    }
                  )
                } else {
                  setLogging(resultResponse.context!!, DatabaseError.NoDataFound)

                  okResponseConfirmOrder.add(
                    ClientConfirmResponse(
                      error = DatabaseError.NoDataFound.noDataFoundError,
                      context = contextFactory.create(messageId = messageId)
                    )
                  )
                }
              }
              else -> {
                setLogging(contextProtocol, DatabaseError.NoDataFound)
                okResponseConfirmOrder.add(
                  ClientConfirmResponse(
                    error = bapResult.body?.error,
                    context = contextProtocol
                  )
                )
              }
            }
          }
          log.info("`Initiated and returning onConfirm acknowledgment`. Message: {}", okResponseConfirmOrder)

          return ResponseEntity.ok(okResponseConfirmOrder)
      } else {
        setLogging(contextFactory.create(), BppError.BadRequestError)
        return mapToErrorResponse(BppError.BadRequestError)
      }
    /*} else {
      setLogging(contextFactory.create(), BppError.AuthenticationError)
      return mapToErrorResponse(
        BppError.AuthenticationError
      )
    }*/
  }

  private fun setLogging(context: ProtocolContext, error: HttpError?) {
    val loggerRequest = loggingFactory.create(messageId = context.messageId,
      transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
      action = ProtocolContext.Action.ON_CANCEL, bppId = context.bppId, errorCode = error?.error()?.code,
      errorMessage = error?.error()?.message
    )
    loggingService.postLog(loggerRequest)
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext? = null) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientConfirmResponse(
          context = context,
          error = it.error()
        )
      )
    )
}