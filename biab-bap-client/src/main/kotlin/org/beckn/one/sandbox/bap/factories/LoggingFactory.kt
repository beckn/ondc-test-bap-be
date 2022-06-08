package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.client.external.logging.LoggingRequest
import org.beckn.protocol.schemas.ProtocolContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime

@Component
class LoggingFactory @Autowired constructor(
  @Value("\${context.domain}") private val domain: String,
  @Value("\${context.city}") private val city: String,
  @Value("\${context.country}") private val country: String,
  @Value("\${context.bap_id}") private val bapId: String,
  @Value("\${context.bap_uri}") private val bapUrl: String,
  @Value("\${logging_service.subscriber-type}") private val subscriberBapType: String,
  @Value("\${logging_service.subscriber-id}") private val subscriberBapId: String,
  private val uuidFactory: UuidFactory,
  private val clock: Clock = Clock.systemUTC()
) {
  fun create(
    transactionId: String = uuidFactory.create(),
    messageId: String = uuidFactory.create(),
    action: ProtocolContext.Action? = ProtocolContext.Action.SEARCH,
    bppId: String? = null,
    contextTimestamp: String? = null,
    contextKey: String?= null,
    contextTTL: String?= null,
    subscriberType : String = subscriberBapType,
    subscriberId: String?= subscriberBapId,
    errorCode: String?= null,
    errorMessage: String?= null
  ) = LoggingRequest(
    context_domain = domain,
    context_country = country,
    context_city = city,
    context_action = action?.value,
    context_core_version = ProtocolVersion.V0_9_1.value,
    context_bap_id = bapId,
    context_bap_uri = bapUrl,
    context_transaction_id = transactionId,
    context_message_id = messageId,
    context_timestamp = contextTimestamp,
    context_key = contextKey,
    context_ttl = contextTTL,
    subscriber_type = subscriberType,
    subscriber_id = subscriberId,
    logged_at = OffsetDateTime.now(clock).toString(),
    error_code = errorCode,
    error_message = errorMessage,
  )
}
