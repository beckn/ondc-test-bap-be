package org.beckn.one.sandbox.bap.client.order.init.controllers

import org.beckn.one.sandbox.bap.client.order.init.services.InitOrderService
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderRequestDto
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
class InitOrderController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val initOrderService: InitOrderService,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/initialize_order")
  @ResponseBody
  fun initiOrderV1(
    @RequestBody orderRequest: OrderRequestDto
  ): ResponseEntity<ProtocolAckResponse> {
    val context = getContext(orderRequest.context.transactionId)
    setLogging(context, null, null)
    return initOrderService.initOrder(
      context = context,
      order = orderRequest.message
    )
      .fold(
        {
          log.error("Error when initializing order: {}", it)
          setLogging(context, it, null)
          mapToErrorResponse(it, context)
        },
        {
          log.info("Successfully initialized order. Message: {}", it)
          setLogging(context, null, it)
          ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
        }
      )
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(
      ProtocolAckResponse(
        context = context,
        message = it.message(),
        error = it.error()
      )
    )

  @PostMapping("/client/v2/initialize_order")
  @ResponseBody
  fun initializeOrderV2(
    @RequestBody orderRequest: List<OrderRequestDto>
  ): ResponseEntity<List<ProtocolAckResponse>> {
    var okResponseInit : MutableList<ProtocolAckResponse> = ArrayList()
    if(!orderRequest.isNullOrEmpty()) {
      for (data in orderRequest) {
        val context = getContext(data.context.transactionId)
        setLogging(context, null, null)
        initOrderService.initOrder(
          context = context,
          order = data.message
        )
          .fold(
            {
              log.error("Error when initializing order: {}", it)
              setLogging(context, it, null)
              okResponseInit.add(ProtocolAckResponse(context = context, message = it.message(), error = it.error()))
            },
            {
              log.info("Successfully initialized order. Message: {}", it)
              setLogging(context, null, it)
              okResponseInit.add(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
            }
          )
      }
      return ResponseEntity.ok(okResponseInit)
    }else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
          listOf(ProtocolAckResponse(
            context = null,message = ResponseMessage.nack() ,
            error = BppError.BadRequestError.badRequestError
          ))
        )
    }
  }

  private fun setLogging(context: ProtocolContext, error: HttpError?, protocolAckResponse: ProtocolAckResponse?) {
    val loggerRequest = loggingFactory.create(messageId = context.messageId,
      transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
      action = context.action, bppId = context.bppId, errorCode = error?.error()?.code,
      errorMessage = error?.error()?.message
    )
    loggingService.postLog(loggerRequest)
  }

  private fun getContext(transactionId: String) =
    contextFactory.create(action = ProtocolContext.Action.INIT, transactionId = transactionId)
}