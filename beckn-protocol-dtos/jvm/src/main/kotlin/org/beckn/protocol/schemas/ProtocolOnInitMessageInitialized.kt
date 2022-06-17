package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolOnInitMessageInitialized @Default constructor(
  val provider: ProtocolOnInitMessageInitializedProvider? = null,
  val providerLocation: ProtocolOnInitMessageInitializedProviderLocation? = null,
  val items: List<ProtocolOnInitMessageInitializedItems>? = null,
  val addOns: List<ProtocolOnInitMessageInitializedAddOns>? = null,
  val offers: List<ProtocolOnInitMessageInitializedOffers>? = null,
  val billing: ProtocolBilling? = null,
  val fulfillment: ProtocolFulfillment? = null,
  val quote: ProtocolQuotation? = null,
  val payment: ProtocolPayment? = null,
  @JsonProperty("@ondc/org/cancellation") val ondcCancellation: ProtocolOndcOrderCancellation? = null,
  @JsonProperty("@ondc/org/linked_orders") val ondcLinkedOrders: List<ProtocolOndcLinkedOrders>? = null,
)

data class ProtocolOnInitMessageInitializedProviderLocation @Default constructor(
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedProvider @Default constructor(
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedItems @Default constructor(
  val id: String? = null,
  val quantity: ProtocolItemQuantityAllocated? = null,
  @JsonProperty("@ondc/org/returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("@ondc/org/cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("@ondc/org/seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("@ondc/org/return_window") val ondcReturnWindow: String?,
  @JsonProperty("@ondc/org/time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("@ondc/org/available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("@ondc/org/statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  @JsonProperty("@ondc/org/statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,

)

// TODO: Example of inline declaration
data class ProtocolItemQuantityAllocated @Default constructor(
  val count: Int? = null,
  val measure: ProtocolScalar? = null
)

data class ProtocolOnInitMessageInitializedAddOns @Default constructor(
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedOffers @Default constructor(
  val id: String? = null
)

