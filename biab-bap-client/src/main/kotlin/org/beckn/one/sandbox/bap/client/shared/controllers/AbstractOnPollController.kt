package org.beckn.one.sandbox.bap.client.shared.controllers

import org.beckn.one.sandbox.bap.client.shared.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import retrofit2.Call

open class AbstractOnPollController<Protocol: ProtocolResponse, Output: ClientResponse>(
  private val onPollService: GenericOnPollService<Protocol, Output>,
  private val contextFactory: ContextFactory,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService
) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun onPoll(
    messageId: String,
    call: Call<List<Protocol>>,
    action: ProtocolContext.Action?
  ): ResponseEntity<out ClientResponse> = onPollService
    .onPoll(contextFactory.create(messageId = messageId), call)
    .fold(
      {
        log.error("Error when finding response by message id. Error: {}", it)
        val context = contextFactory.create(messageId = messageId)
        val loggerRequest = loggingFactory.create(messageId = messageId, transactionId = context.transactionId,
          contextTimestamp = context.timestamp.toString(),
          action = action, bppId = context.bppId,errorCode = it.error().code, errorMessage = it.error().code
        )
        loggingService.postLog(loggerRequest)
        ResponseEntity
          .status(it.status().value())
          .body(ClientErrorResponse(context = context, error = it.error()))
      },
      {
        val context = contextFactory.create(messageId = messageId)
        log.info("Found responses for message {}", messageId)
        val loggerRequest = loggingFactory.create(messageId = messageId, transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
          action = action, bppId = context.bppId
        )
        loggingService.postLog(loggerRequest)
        ResponseEntity.ok(it)
      }
    )
}