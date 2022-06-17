package org.beckn.protocol.schemas

data class ProtocolAddress @Default constructor(
  val door: String? = null,
  val name: String? = null,
  val building: String? = null,
  val street: String? = null,
  val locality: String? = null,
  val ward: String? = null,
  val city: String? = null,
  val state: String? = null,
  val country: String? = null,
  val areaCode: String? = null
)
