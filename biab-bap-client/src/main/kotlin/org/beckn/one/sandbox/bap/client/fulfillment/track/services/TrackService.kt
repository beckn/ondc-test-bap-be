package org.beckn.one.sandbox.bap.client.fulfillment.track.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.order.quote.services.QuoteService
import org.beckn.one.sandbox.bap.client.shared.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TrackService @Autowired constructor(
  private val registryService: RegistryService,
  private val bppTrackService: BppTrackService,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,
) {
  private val log: Logger = LoggerFactory.getLogger(QuoteService::class.java)

  fun track(context: ProtocolContext, request: TrackRequestDto): Either<HttpError, ProtocolAckResponse?> {
    log.info("Got track request. Request: {}", request)

    return TrackRequestDto.validate(request)
      .flatMap {
        registryService.lookupBppById(it.context.bppId!!)
      }.map {
        setLogging(context,it.first().subscriber_id, SubscriberDto.Type.LREG.name)
        it.first()
      }.flatMap {
        bppTrackService.track(it.subscriber_url, context, request)
      }
  }

  private fun setLogging(context: ProtocolContext, subscriberId: String?, subscriberType: String?) {
    val loggerRequest = loggingFactory.create(messageId = context.messageId, transactionId = context.transactionId,
      contextTimestamp = context.timestamp.toString(),
      action = context.action, bppId = context.bppId,subscriberId = subscriberId, subscriberType = subscriberType
    )
    loggingService.postLog(loggerRequest)
  }
}
