package org.beckn.protocol.schemas

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
  val ondcCancellation: ProtocolOndcOrderCancellation? = null,
  val ondcLinkedOrders: List<ProtocolOndcLinkedOrders>? = null,
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
  val ondcReturnable: Boolean? = true,
  val ondcCancellable: Boolean? = true,
  val ondcSellerPickupReturn: Boolean? = true,
  val ondcReturnWindow: String?,
  val ondcTimeToShip: String? = null,
  val ondcAvailableOnCod: Boolean? = true,
  val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,

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

