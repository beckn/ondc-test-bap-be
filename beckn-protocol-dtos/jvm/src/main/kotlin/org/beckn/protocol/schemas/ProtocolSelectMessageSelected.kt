package org.beckn.protocol.schemas

data class ProtocolOnSelectMessageSelected @Default constructor(
  val provider: ProtocolProvider? = null,
  val providerLocation: ProtocolLocation? = null,
  val items: List<ProtocolOnSelectedItem>? = null,
  val addOns: List<ProtocolAddOn>? = null,
  val offers: List<ProtocolOffer>? = null,
  val quote: ProtocolQuotation? = null,
  val ondcCancellation: ProtocolOndcOrderCancellation? = null,
  val ondcLinkedOrders: List<ProtocolOndcLinkedOrders>? = null,
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
  val ondcReturnable: Boolean? = true,
  val ondcCancellable: Boolean? = true,
  val ondcSellerPickupReturn: Boolean? = true,
  val ondcReturnWindow: String?,
  val ondcTimeToShip: String? = null,
  val ondcAvailableOnCod: Boolean? = true,
  val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
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
  val ondcReturnable: Boolean? = true,
  val ondcCancellable: Boolean? = true,
  val ondcSellerPickupReturn: Boolean? = true,
  val ondcReturnWindow: String?,
  val ondcTimeToShip: String? = null,
  val ondcAvailableOnCod: Boolean? = true,
  val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
)

data class ProtocolItemQuantity @Default constructor(
  val allocated: ProtocolItemQuantityAllocated? = null,
  val available: ProtocolItemQuantityAllocated? = null,
  val maximum: ProtocolItemQuantityAllocated? = null,
  val minimum: ProtocolItemQuantityAllocated? = null,
  val selected: ProtocolItemQuantityAllocated? = null
)
