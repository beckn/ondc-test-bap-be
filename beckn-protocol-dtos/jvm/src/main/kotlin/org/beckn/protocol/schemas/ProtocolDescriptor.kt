package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolDescriptor @Default constructor(
  val name: String?,
  val code: String? = null,
  val symbol: String? = null,
  val shortDesc: String? = null,
  val longDesc: String? = null,
  val images: List<String>? = null,
  val audio: String? = null,
  @JsonProperty("3d_render") val threeDRender: String? = null
)
