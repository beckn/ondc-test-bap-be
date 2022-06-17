package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolOnSelectMessageSelected @Default constructor(
  val provider: ProtocolProvider? = null,
  val providerLocation: ProtocolLocation? = null,
  val items: List<ProtocolOnSelectedItem>? = null,
  val addOns: List<ProtocolAddOn>? = null,
  val offers: List<ProtocolOffer>? = null,
  val quote: ProtocolQuotation? = null,
  @JsonProperty("@ondc/org/cancellation") val ondcCancellation: ProtocolOndcOrderCancellation? = null,
  @JsonProperty("@ondc/org/linked_orders") val ondcLinkedOrders: List<ProtocolOndcLinkedOrders>? = null,
)

data class ProtocolSelectMessageSelected @Default constructor(
  val provider: ProtocolProvider? = null,
  val providerLocation: ProtocolLocation? = null,
  val items: List<ProtocolSelectedItem>? = null,
  val addOns: List<ProtocolAddOn>? = null,
  val offers: List<ProtocolOffer>? = null,
  val quote: ProtocolQuotation? = null
)

data class ProtocolOnSelectedItem @Default constructor(
  val id: String,
  val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: ProtocolTime? = null,
  val tags: Map<String, String>? = null,
  val quantity: ProtocolItemQuantity,
  @JsonProperty("@ondc/org/returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("@ondc/org/cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("@ondc/org/seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("@ondc/org/return_window") val ondcReturnWindow: String?,
  @JsonProperty("@ondc/org/time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("@ondc/org/available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("@ondc/org/statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  @JsonProperty("@ondc/org/statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
)

data class ProtocolSelectedItem @Default constructor(
  val id: String,
  val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: ProtocolTime? = null,
  val tags: Map<String, String>? = null,
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

data class ProtocolItemQuantity @Default constructor(
  val allocated: ProtocolItemQuantityAllocated? = null,
  val available: ProtocolItemQuantityAllocated? = null,
  val maximum: ProtocolItemQuantityAllocated? = null,
  val minimum: ProtocolItemQuantityAllocated? = null,
  val selected: ProtocolItemQuantityAllocated? = null
)
