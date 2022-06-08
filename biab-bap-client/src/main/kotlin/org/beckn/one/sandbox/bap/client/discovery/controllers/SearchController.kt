package org.beckn.one.sandbox.bap.client.discovery.controllers

import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.discovery.services.SearchService
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchRequestDto
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
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
      contextFactory.create(transactionId = request.context.transactionId, bppId = request.context.bppId, action = ProtocolContext.Action.SEARCH)
    val loggerRequest = loggingFactory.create(messageId = protocolContext.messageId,
      transactionId = protocolContext.transactionId, contextTimestamp = protocolContext.timestamp.toString(),
      action = protocolContext.action, bppId = protocolContext.bppId
    )
    loggingService.postLog(loggerRequest)
    return searchService.search(protocolContext, request.message.criteria)
      .fold(
        {
          log.error("Error during search. Error: {}", it)
          val loggerRequest = loggingFactory.create(messageId = protocolContext.messageId, transactionId = protocolContext.transactionId, contextTimestamp = protocolContext.timestamp.toString(),
            action = protocolContext.action, bppId = protocolContext.bppId, errorMessage = it.error().message, errorCode = it.error().code
          )
          loggingService.postLog(loggerRequest)
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
}