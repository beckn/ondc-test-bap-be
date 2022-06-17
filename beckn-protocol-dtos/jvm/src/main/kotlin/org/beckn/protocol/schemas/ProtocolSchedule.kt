package org.beckn.protocol.schemas

data class ProtocolSchedule @Default constructor(
  val frequency: String? = null,
  val holidays: List<java.time.OffsetDateTime>? = null,
  val times: List<java.time.OffsetDateTime>? = null
)