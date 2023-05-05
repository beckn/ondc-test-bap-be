package org.beckn.one.sandbox.bap.client.order.status.controllers

import org.beckn.one.sandbox.bap.client.order.status.services.OrderStatusService
import org.beckn.one.sandbox.bap.client.order.status.singleton.Order.Companion.order_object
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderStatusDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderStatusController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val orderStatusService: OrderStatusService,
  val loggingFactory: LoggingFactory,
  val loggingService: LoggingService,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/order_status")
  @ResponseBody
  fun orderStatusV1(
    @RequestBody orderStatusRequest: OrderStatusDto
  ): ResponseEntity<ProtocolAckResponse> {
    val context = getContext(orderStatusRequest.context.transactionId)
    return orderStatusService.getOrderStatus(
      context = context,
      request = orderStatusRequest
    )
      .fold(
        {
          log.error("Error when getting order status: {}", it)
          setLogging(context, it)
          mapToErrorResponseV1(it, context)
        },
        {
          log.info("Successfully triggered order status api. Message: {}", it)
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

  @PostMapping("/client/v2/order_status")
  @ResponseBody
  fun orderStatusV2(
    @RequestBody orderStatusRequest: List<OrderStatusDto>
  ): ResponseEntity<List<ProtocolAckResponse>> {
    var okResponseOrderStatus : MutableList<ProtocolAckResponse> = ArrayList()

    if(!orderStatusRequest.isNullOrEmpty()){
      for( data: OrderStatusDto in orderStatusRequest) {
        val context = getContext(data.context.transactionId)
        order_object = data.orderObject
        orderStatusService.getOrderStatus(
          context = context,
          request = data
        ).fold(
            {
              log.error("Error when getting order status: {}", it)
              setLogging(context, it)
              okResponseOrderStatus.add( ProtocolAckResponse(
                context = context,
                message = it.message(),
                error = it.error()
              ))
            },
            {
              log.info("Successfully triggered order status api. Message: {}", it)
              setLogging(context, null)
              okResponseOrderStatus.add( ProtocolAckResponse(
                context = context, message = ResponseMessage.ack()
              ))
            }
          )
      }
      return ResponseEntity.ok(okResponseOrderStatus)
    }else {
      setLogging(contextFactory.create(action = ProtocolContext.Action.STATUS), BppError.BadRequestError)

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
          listOf(
            ProtocolAckResponse(
              context = null, message = ResponseMessage.nack(),
              error = BppError.BadRequestError.badRequestError
            )
          )
        )
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

  private fun getContext(transactionId: String) =
    contextFactory.create(action = ProtocolContext.Action.STATUS, transactionId = transactionId)
}