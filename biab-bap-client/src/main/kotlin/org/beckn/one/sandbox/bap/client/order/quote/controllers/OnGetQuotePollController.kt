package org.beckn.one.sandbox.bap.client.order.quote.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext

import org.beckn.protocol.schemas.ProtocolOnSelect

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnGetQuotePollController @Autowired constructor(
  val onPollService: GenericOnPollService<ProtocolOnSelect, ClientQuoteResponse>,
  val contextFactory: ContextFactory,
  private val protocolClient: ProtocolClient,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,
) : AbstractOnPollController<ProtocolOnSelect, ClientQuoteResponse>(onPollService, contextFactory, loggingFactory, loggingService) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/on_get_quote")
  @ResponseBody
  fun onGetQuoteV1(@RequestParam messageId: String): ResponseEntity<out ClientResponse> =
    onPoll(messageId, protocolClient.getSelectResponsesCall(messageId), ProtocolContext.Action.ON_SELECT)

  @RequestMapping("/client/v2/on_get_quote")
  @ResponseBody
  fun onGetQuoteV2(@RequestParam messageIds: String): ResponseEntity<out List<ClientResponse>> {

    if (messageIds.isNotEmpty() && messageIds.trim().isNotEmpty()) {
      val messageIdArray = messageIds.split(",")
      var okResponseQuotes: MutableList<ClientQuoteResponse> = ArrayList()
       for (msgId in messageIdArray) {
         val context = contextFactory.create(messageId = msgId)
         setLogging(context, null)
          onPollService.onPoll(
            context ,
            protocolClient.getSelectResponsesCall(msgId)
          ).fold(
            {
              setLogging(context, it)
              okResponseQuotes.add(
                ClientQuoteResponse(
                  context = context,
                  error = it.error(), message = null
                )
              )
            }, {
              setLogging(context, null)
              okResponseQuotes.add(it)
            }
          )
        }
        log.info("`Initiated and returning on quotes polling result`. Message: {}", okResponseQuotes)
        return ResponseEntity.ok(okResponseQuotes)
    } else {
      val loggerRequest = loggingFactory.create(
        action = ProtocolContext.Action.ON_SELECT, errorCode = BppError.BadRequestError.badRequestError.code,
        errorMessage = BppError.BadRequestError.badRequestError.message
      )
      loggingService.postLog(loggerRequest)
      return mapToErrorResponse(BppError.BadRequestError)
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

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientQuoteResponse(
          error = it.error(),
          context = null
        )
      )
    )
}