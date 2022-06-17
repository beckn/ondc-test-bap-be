package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ProtocolCatalog @Default constructor(
  @JsonProperty("bpp/descriptor") val bppDescriptor: ProtocolDescriptor? = null,
  @JsonProperty("bpp/providers") val bppProviders: List<ProtocolProviderCatalog>? = null,
  @JsonProperty("bpp/categories") val bppCategories: List<ProtocolCategory>? = null,
  val id: String? = null,
  val exp: LocalDateTime? = null
)

data class ProtocolProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val locations: List<ProtocolLocation>? = null,
  val categories: List<ProtocolCategory>? = null,
  val items: List<ProtocolItem>? = null,
  val fulfillments: List<ProtocolFulfillment>? = null,
  val tags: Map<String, String>? = null,
  val exp: LocalDateTime? = null,
  val matched: Boolean? = null
)