package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolOrder @Default constructor(
  val provider: ProtocolSelectMessageSelectedProvider? = null,
  val items: List<ProtocolSelectMessageSelectedItems>,
  val addOns: List<ProtocolSelectMessageSelectedAddOns>?,
  val offers: List<ProtocolSelectMessageSelectedOffers>?,
  val billing: ProtocolBilling,
  val fulfillment: ProtocolFulfillment,
  val quote: ProtocolQuotation? = null,
  val payment: ProtocolPayment? = null, //todo: is this surely nullable?
  val id: String? = null,
  val state: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null,
  @JsonProperty("@ondc/org/cancellation") val ondcCancellation: ProtocolOndcOrderCancellation? = null,
  @JsonProperty("@ondc/org/linked_orders") val ondcLinkedOrders: List<ProtocolOndcLinkedOrders>? = null,

)


data class ProtocolSelectMessageSelectedProvider @Default constructor(
  val id: String,
  val locations: List<ProtocolSelectMessageSelectedProviderLocations>?
)

data class ProtocolSelectMessageSelectedProviderLocations @Default constructor(
  val id: String
)

// TODO can be common
data class ProtocolSelectMessageSelectedAddOns @Default constructor(
  val id: String
)

// TODO similar to OnInitMessageInitializedItems
data class ProtocolSelectMessageSelectedItems @Default constructor(
  val id: String,
  val quantity: ProtocolItemQuantityAllocated,
  @JsonProperty("@ondc/org/returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("@ondc/org/cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("@ondc/org/seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("@ondc/org/return_window") val ondcReturnWindow: String?,
  @JsonProperty("@ondc/org/time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("@ondc/org/available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("@ondc/org/statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  @JsonProperty("@ondc/org/statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
)

data class ProtocolSelectMessageSelectedOffers @Default constructor(
  val id: String? = null
)

data class ProtocolOndcOrderCancellation @Default constructor(
  val type: OndcCancellationType? = null,
  val refId: String? = null,
  val policies: ProtocolPolicy? = null,
  val time: java.time.OffsetDateTime? = null,
  val cancelledBy: String? = null,
  val reasons: ProtocolOption? = null,
  val selectedReason: ProtocolSelectedReason? = null,
  val additionalDescription: ProtocolDescriptor? = null,
) {
  enum class OndcCancellationType(val value: String) {
    @JsonProperty(" full")
    FULL("full"),
    @JsonProperty("partial")
    PARTIAL("partial");
  }
}

data class ProtocolOndcLinkedOrders @Default constructor(
  val id: String
)
data class ProtocolSelectedReason @Default constructor(
  val id: String
)

data class ProtocolPolicy @Default constructor(
  val id: String?= null,
  val parentPolicyId: String?= null,
  val descriptor: ProtocolDescriptor? = null,
  val time: ProtocolTime? = null,
)