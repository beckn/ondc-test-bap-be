package org.beckn.one.sandbox.bap.client.order.status.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.status.services.OnOrderStatusService
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderStatusResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.mappers.OnOrderProtocolToEntityOrder
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnOrderStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class OnOrderStatusPollController(
  onPollService: GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>,
  val contextFactory: ContextFactory,
  val mapping: OnOrderProtocolToEntityOrder,
  val protocolClient: ProtocolClient,
  val onOrderStatusService: OnOrderStatusService,
  val loggingFactory: LoggingFactory,
  val loggingService: LoggingService,
) : AbstractOnPollController<ProtocolOnOrderStatus, ClientOrderStatusResponse>(onPollService, contextFactory, loggingFactory, loggingService) {

  @RequestMapping("/client/v1/on_order_status")
  @ResponseBody
  fun onOrderStatusV1(@RequestParam orderId: String): ResponseEntity<out ClientResponse> =
    onPoll(orderId, protocolClient.getOrderByIdStatusResponsesCall(orderId), ProtocolContext.Action.ON_STATUS)

  @RequestMapping("/client/v2/on_order_status")
  @ResponseBody
  fun onOrderStatusV2(@RequestParam orderIds: String): ResponseEntity<out List<ClientResponse>> {

    if (orderIds.isNotEmpty() && orderIds.trim().isNotEmpty()) {
      val orderIdArray = orderIds.split(",")
      var okResponseOnOrderStatus: MutableList<ClientResponse> = ArrayList()
        //if (SecurityUtil.getSecuredUserDetail() != null) {
          val user = SecurityUtil.getSecuredUserDetail()
          for (orderId in orderIdArray) {
            val messageId = contextFactory.create().messageId
            val bapResult = onPoll(
              messageId,
              protocolClient.getOrderByIdStatusResponsesCall(orderId),
              ProtocolContext.Action.ON_STATUS
            )
            when (bapResult.statusCode.value()) {
              200 -> {
                  val resultResponse = bapResult.body as ClientOrderStatusResponse
                if (resultResponse.message?.order != null) {
                  val orderDao: OrderDao = mapping.protocolToEntity(resultResponse.message?.order!!)
                  orderDao.transactionId = resultResponse.context.transactionId
                  orderDao.userId = user?.uid
                  orderDao.messageId = resultResponse.context.messageId
                  onOrderStatusService.updateOrder(orderDao).fold(
                    {
                      okResponseOnOrderStatus.add(
                        ClientErrorResponse(
                          context = contextFactory.create(messageId = resultResponse.context.messageId),
                          error = it.error()
                        )
                      )
                    }, {
                      okResponseOnOrderStatus.add(resultResponse)
                    }
                  )
                }else{
                  okResponseOnOrderStatus.add(
                    ClientErrorResponse(
                      context = contextFactory.create(messageId = resultResponse.context.messageId),
                      error = bapResult.body?.error
                    )
                  )
                }
              }
              else -> {
                setLogging(contextFactory.create(messageId= messageId, action = ProtocolContext.Action.ON_STATUS), BppError.Nack)
                okResponseOnOrderStatus.add(
                  ClientErrorResponse(
                    context = contextFactory.create(messageId= messageId, action = ProtocolContext.Action.ON_STATUS),
                    error = bapResult.body?.error
                  )
                )
              }
            }
          }
        /*}else{
          setLogging(contextFactory.create(action = ProtocolContext.Action.ON_STATUS), BppError.AuthenticationError)
          return mapToErrorResponseV2(BppError.AuthenticationError)
        }*/
        return ResponseEntity.ok(okResponseOnOrderStatus)
    } else {
      setLogging(contextFactory.create(action = ProtocolContext.Action.ON_STATUS), BppError.BadRequestError)
      return mapToErrorResponseV2(BppError.BadRequestError)
    }
  }

  private fun mapToErrorResponseV2(it: HttpError, context: ProtocolContext? = null) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientErrorResponse(
          context = context,
          error = it.error()
        )
      )
    )

  private fun setLogging(context: ProtocolContext, error: HttpError?) {
    val loggerRequest = loggingFactory.create(messageId = context.messageId,
      transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
      action = context.action, bppId = context.bppId, errorCode = error?.error()?.code,
      errorMessage = error?.error()?.message
    )
    loggingService.postLog(loggerRequest)
  }

}
