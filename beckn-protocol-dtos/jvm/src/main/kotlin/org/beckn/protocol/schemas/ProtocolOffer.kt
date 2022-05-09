package org.beckn.protocol.schemas

data class ProtocolOffer(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val locationIds: List<String>? = null,
  val categoryIds: List<String>? = null,
  val itemIds: List<String>? = null,
  val time: ProtocolTime? = null
)