package org.beckn.one.sandbox.bap.protocol.shared.controllers

import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.protocol.shared.services.PollForResponseService
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolErrorResponse
import org.beckn.protocol.schemas.ProtocolResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity

open class AbstractPollForResponseController<Protocol: ProtocolResponse>(
  private val responseService: PollForResponseService<Protocol>,
  private val contextFactory: ContextFactory,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService
) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun findResponses(
    messageId: String,
    action: ProtocolContext.Action
  ): ResponseEntity<List<ProtocolResponse>> = responseService
    .findResponses(messageId)
    .fold(
      {
        val protocolContext  = contextFactory.create(messageId = messageId)
        val loggerRequest = loggingFactory.create(messageId = protocolContext.messageId, transactionId = protocolContext.transactionId, contextTimestamp = protocolContext.timestamp.toString(),
          action = action, bppId = protocolContext.bppId, errorMessage = it.error().message, errorCode = it.error().code
        )
        loggingService.postLog(loggerRequest)
        log.error("Error when finding search response by message id. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(listOf(ProtocolErrorResponse(context = protocolContext, error = it.error())))
      },
      {
        val protocolContext  = contextFactory.create(messageId = messageId)
        val loggerRequest = loggingFactory.create(messageId = protocolContext.messageId, transactionId = protocolContext.transactionId, contextTimestamp = protocolContext.timestamp.toString(),
          action = action, bppId = protocolContext.bppId
        )
        loggingService.postLog(loggerRequest)

        log.info("Found responses for message {}", messageId)
        ResponseEntity.ok(it)
      }
    )

  fun findResponsesByOrderId(
    orderId: String
  ): ResponseEntity<List<ProtocolResponse>> = responseService
    .findResponsesByOrderId(orderId)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        val loggerRequest = loggingFactory.create(
          action = ProtocolContext.Action.ON_STATUS,
          errorCode = it.error().code,
          errorMessage = it.error().message
        )
        loggingService.postLog(loggerRequest)
        ResponseEntity
          .status(it.status().value())
          .body(listOf(ProtocolErrorResponse(context = contextFactory.create(), error = it.error())))
      },
      {
        log.info("Found responses for order {}", orderId)
        val loggerRequest = loggingFactory.create(
          messageId = it.first().context?.messageId,
          transactionId = it.first().context?.transactionId,
          contextTimestamp = it.first().context?.timestamp.toString(),
          action = ProtocolContext.Action.ON_STATUS, bppId = it.first().context?.bppId
        )
        loggingService.postLog(loggerRequest)

        ResponseEntity.ok(it)
      }
    )
}