package org.beckn.one.sandbox.bap.client.order.quote.controllers

import org.beckn.one.sandbox.bap.client.order.quote.services.QuoteService
import org.beckn.one.sandbox.bap.client.shared.dtos.GetQuoteRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolContext.Action.SELECT
import org.beckn.protocol.schemas.ProtocolError
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
class GetQuoteController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val quoteService: QuoteService,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/get_quote")
  @ResponseBody
  fun getQuoteV1(@RequestBody request: GetQuoteRequestDto): ResponseEntity<ProtocolAckResponse> {
    val context = getContext(request.context.transactionId)
    setLogging(context, null, null)
    return quoteService.getQuote(context, request.message.cart)
      .fold(
        {
          log.error("Error when getting quote: {}", it)
          setLogging(context, it, null)
          mapToErrorResponseV1(it, context)
        },
        {
          log.info("Successfully initiated get quote. Message: {}", it)
          setLogging(context, null, it)
          ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
        }
      )
  }

  private fun mapToErrorResponseV1(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(ProtocolAckResponse(context = context, message = it.message(), error = it.error()))


  @PostMapping("/client/v2/get_quote")
  @ResponseBody
  fun getQuoteV2(@RequestBody request: List<GetQuoteRequestDto>): ResponseEntity<List<ProtocolAckResponse>> {
    var okResponseQuotes : MutableList<ProtocolAckResponse> = ArrayList()

    if(!request.isNullOrEmpty()){
      for( quoteRequest:GetQuoteRequestDto in request){
        val context = getContext(quoteRequest.context.transactionId)
        setLogging(context, null, null)
        quoteService.getQuote(context, quoteRequest.message.cart)
          .fold(
            {
              log.error("Error when getting quote: {}", it)
              setLogging(context, it, null)
              okResponseQuotes.add(ProtocolAckResponse(context = context, message = it.message(), error = it.error()))
            },
            {
              log.info("`Successfully initiated get quote`. Message: {}", it)
              setLogging(context, null, it)
              okResponseQuotes.add(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
            }
          )
      }
      log.info("`Initiated and returning quotes acknowledgment`. Message: {}", okResponseQuotes)
      return ResponseEntity.ok(okResponseQuotes)
    }else {
      val loggerRequest = loggingFactory.create(
        action = SELECT,  errorCode = BppError.BadRequestError.badRequestError.code,
        errorMessage = BppError.BadRequestError.badRequestError.message
      )
      loggingService.postLog(loggerRequest)
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
          listOf(ProtocolAckResponse(
            context = null,message = ResponseMessage.nack() ,
            error = BppError.BadRequestError.error()))
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


  private fun getContext(transactionId: String) = contextFactory.create(action = SELECT, transactionId = transactionId)
}