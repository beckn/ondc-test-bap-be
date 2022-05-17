package org.beckn.one.sandbox.bap.client.discovery.controllers

import org.beckn.one.sandbox.bap.client.discovery.services.SearchService
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchRequestDto
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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController @Autowired constructor(
  val searchService: SearchService,
  val contextFactory: ContextFactory,
  val loggingService: LoggingService,
  val loggingFactory: LoggingFactory
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/search")
  @ResponseBody
  fun searchV1(@RequestBody request: SearchRequestDto): ResponseEntity<ProtocolAckResponse> {
    val protocolContext =
      contextFactory.create(
        transactionId = request.context.transactionId,
        bppId = request.context.bppId,
        action = ProtocolContext.Action.SEARCH
      )
    setLogging(protocolContext, null)

    return searchService.search(protocolContext, request.message.criteria)
      .fold(
        {
          log.error("Error during search. Error: {}", it)
          setLogging(protocolContext, it)
          ResponseEntity
            .status(it.status().value())
            .body(ProtocolAckResponse(protocolContext, it.message(), it.error()))
        },
        {
          log.info("Successfully initiated Search")
          ResponseEntity.ok(ProtocolAckResponse(protocolContext, ResponseMessage.ack()))
        }
      )
  }


  private fun setLogging(context: ProtocolContext?, error: HttpError?) {

    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = context.action, bppId = context.bppId, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.SEARCH,
        errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message)
    }

    loggingService.postLog(loggerRequest)
  }
}