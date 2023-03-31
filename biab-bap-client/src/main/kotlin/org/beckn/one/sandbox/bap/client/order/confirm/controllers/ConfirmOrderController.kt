package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.order.confirm.services.ConfirmOrderService
import org.beckn.one.sandbox.bap.client.shared.Util
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ResponseMessage
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ConfirmOrderController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val confirmOrderService: ConfirmOrderService,
  private val confirmOrderRepository: ResponseStorageService<OrderResponse, OrderDao>,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/confirm_order")
  @ResponseBody
  fun confirmOrderV1(
    @RequestBody orderRequest: OrderRequestDto
  ): ResponseEntity<ProtocolAckResponse> {
    val context = getContext(orderRequest.context.transactionId)
    setLogging(context, null)
    return confirmOrderService.confirmOrder(
      context = context,
      order = orderRequest.message
    ).fold(
        {
          log.error("Error when confirming order: {}", it)
          setLogging(context, it)
          mapToErrorResponseV1(it, context)
        },
        {
          log.info("Successfully confirmed order. Message: {}", it)
          setLogging(context, null)
          ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
        }
      )
  }

  private fun mapToErrorResponseV1(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(
      ProtocolAckResponse(
        context = context,
        message = it.message(),
        error = it.error()
      )
    )



  @PostMapping("/client/v2/confirm_order")
  @ResponseBody
  fun confirmOrderV2(
    @RequestBody orderRequest: List<OrderRequestDto>
  ): ResponseEntity<List<ProtocolAckResponse>> {

    var okResponseConfirmOrders: MutableList<ProtocolAckResponse> = ArrayList()
    if (!orderRequest.isNullOrEmpty()) {
      //if (SecurityUtil.getSecuredUserDetail() != null) {
        val parentOrderId = Util.getRandomString()
        for (order in orderRequest) {
          val context = getContext(order.context.transactionId)
          setLogging(context, null)
          confirmOrderService.confirmOrder(
            context = context,
            order = order.message
          ).fold(
              {
                log.error("Error when confirming order: {}", it)
                setLogging(context, it)
                okResponseConfirmOrders.add(
                  ProtocolAckResponse(
                    context = context,
                    message = it.message(),
                    error = it.error()
                  )
                )
              },
              {
                log.info("Successfully confirmed order. Message: {}", it)
                setLogging(context, null)
                confirmOrderRepository.updateDocByQuery(
                  OrderDao::messageId eq context?.messageId,
                  OrderDao(
                    userId = SecurityUtil.getSecuredUserDetail()?.uid,
                    messageId = context?.messageId,
                    transactionId = null,
                    parentOrderId =  parentOrderId,
                    ondcCancellation = null,
                    ondcLinkedOrders = null
                  )
                ).fold(
                  {
                    log.error("Error when updating order: {}", it)
                    setLogging(context, it)
                    okResponseConfirmOrders.add(
                      ProtocolAckResponse(
                        context = context,
                        message = it.message(),
                        error = it.error()
                      )
                    )

                  },
                  {
                    log.info("Successfully updated  order in client layer db : {}", it)
                    setLogging(context, null)
                    okResponseConfirmOrders.add(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
                  }
                )
              }
            )
        }
        return ResponseEntity.ok(okResponseConfirmOrders)
     /* } else {
        setLogging(contextFactory.create(), BppError.AuthenticationError)
        return mapToErrorResponseV2(BppError.AuthenticationError, null)
      }*/
    } else {
      setLogging(contextFactory.create(), BppError.BadRequestError)
      return mapToErrorResponseV2(BppError.BadRequestError, null)
    }
  }

  private fun setLogging(context: ProtocolContext, error: HttpError?) {
    val loggerRequest = loggingFactory.create(messageId = context.messageId,
      transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
      action = context.action, bppId = context.bppId, errorCode = error?.error()?.code,
      errorMessage = error?.error()?.message
    )
    loggingService.postLog(loggerRequest)
  }

  private fun mapToErrorResponseV2(it: HttpError, context: ProtocolContext?) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ProtocolAckResponse(
          context = context,
          message = it.message(),
          error = it.error()
        )
      )
    )

  private fun getContext(transactionId: String) =
    contextFactory.create(action = ProtocolContext.Action.CONFIRM, transactionId = transactionId)
}