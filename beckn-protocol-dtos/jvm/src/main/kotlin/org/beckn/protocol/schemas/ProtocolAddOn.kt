package org.beckn.protocol.schemas

data class ProtocolAddOn @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null
)
