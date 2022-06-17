package org.beckn.protocol.schemas

data class ProtocolProvider @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val time: ProtocolTime? = null,
  val locations: List<ProtocolLocation>? = null,
  val tags: Map<String, String>? = null,
  val category_id : String? = null,
  val rating: Int? = null
)
