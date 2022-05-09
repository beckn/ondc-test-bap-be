package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolOnTrackMessageTracking @Default constructor(
  val url: String? = null,
  val status: ProtocolTrackingStatus? = null,
) {
  enum class ProtocolTrackingStatus {
    @JsonProperty("active")
    Active,

    @JsonProperty("inactive")
    Inactive
  }
}