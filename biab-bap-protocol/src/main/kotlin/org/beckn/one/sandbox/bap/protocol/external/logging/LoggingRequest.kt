package org.beckn.one.sandbox.bap.protocol.external.logging

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Clock

data class LoggingRequest(
  val context_domain: String ,
  val context_country: String ,
  val context_city: String ,
  val context_action: String? ,
  val context_core_version: String? ,
  val context_bap_id: String? ,
  val context_key: String? ,
  val context_bap_uri: String? ,
  val context_transaction_id: String,
  val context_message_id: String,
  @JsonIgnore
  val clock: Clock = Clock.systemUTC(),
  val context_timestamp: String? ,
  val context_ttl: String? ,
  val subscriber_type: String? ,
  val subscriber_id: String? ,
  val logged_at: String? ,
  val error_code: String?,
  val error_message: String?
)
