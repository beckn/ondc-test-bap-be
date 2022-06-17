package org.beckn.protocol.schemas

data class ProtocolIntent(
  val fulfillment: ProtocolFulfillment?,
  val item: ProtocolIntentItem? = null,
  val provider: ProtocolProvider?,
  val category: ProtocolCategory? = null,
  val tags: Map<String, String>? = null
)

data class ProtocolIntentItem(
  val id: String? = null,
  val descriptor: ProtocolIntentItemDescriptor? = null
)

data class ProtocolIntentItemDescriptor(
  val name: String? = null,
  val tags: Map<String, String>? = null
)