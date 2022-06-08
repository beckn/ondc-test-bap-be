package org.beckn.one.sandbox.bap.client.discovery.services

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.extensions.orElse
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils.hasText

@Service
class SearchService @Autowired constructor(
  private val registryService: RegistryService,
  private val gatewayService: GatewayService,
  private val bppSearchService: BppSearchService,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,

) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: ProtocolContext, criteria: SearchCriteria): Either<HttpError, ProtocolAckResponse> {
    log.info("Got search request with criteria: {} ", criteria)
    if (isBppFilterSpecified(context)) {
      return registryService
        .lookupBppById(context.bppId!!)
        .flatMap {
          log.error("Error during search. Error: {}", it)
          val loggerRequest = loggingFactory.create(messageId = context.messageId,
            transactionId = context.transactionId,
            contextTimestamp = context.timestamp.toString(),
            action = context.action, bppId = context.bppId
          )
          loggingService.postLog(loggerRequest)
          bppSearchService.search(it.first().subscriber_url, context, criteria)
        }
    }
    return registryService
      .lookupGateways()
      .flatMap {
        val loggerRequest = loggingFactory.create(messageId = context.messageId, transactionId = context.transactionId,
          contextTimestamp = context.timestamp.toString(),
          action = context.action, bppId = context.bppId
        )
        loggingService.postLog(loggerRequest)

        it.fold(GatewaySearchError.NullResponse.left() as Either<HttpError, ProtocolAckResponse>) { previousGatewayResponse, gateway ->
          previousGatewayResponse.orElse { gatewayService.search(gateway, context, criteria) }
        }
      }
  }

  private fun isBppFilterSpecified(context: ProtocolContext) =
    hasText(context.bppId)

}
