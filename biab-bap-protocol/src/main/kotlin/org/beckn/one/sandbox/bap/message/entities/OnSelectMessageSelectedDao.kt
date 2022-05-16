package org.beckn.one.sandbox.bap.message.entities

import org.beckn.protocol.schemas.Default


data class OnSelectMessageSelectedDao @Default constructor(
  val provider: ProviderDao? = null,
  val providerLocation: LocationDao? = null,
  val items: List<SelectedItemDao>? = null,
  val addOns: List<AddOnDao>? = null,
  val offers: List<OfferDao>? = null,
  val quote: QuotationDao? = null,
  val ondcCancellation: OndcOrderCancellationDao? =  null,
  val ondcLinkedOrders: List<OndcLinkedOrdersDao>? = null,
)

data class SelectedItemDao @Default constructor(
  val id: String? = null,
  val parentItemId: String? = null,
  val descriptor: DescriptorDao? = null,
  val price: PriceDao? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: TimeDao? = null,
  val tags: Map<String, String>? = null,
  val quantity: ItemQuantityDao,
  val ondcReturnable: Boolean? = true,
  val ondcCancellable: Boolean? = true,
  val ondcSellerPickupReturn: Boolean? = true,
  val ondcReturnWindow: String?= null,
  val ondcTimeToShip: String? = null,
  val ondcAvailableOnCod: Boolean? = true,
  val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommoditiesDao? = null,
  val ondcStatutoryPackagedFood: OndcStatutoryPackagedFoodDao? = null,
  val ondcContactDetailsConsumerCare: String?,
  val ondcMandatoryReqsVeggiesFruits: String?,
  val rateable: Boolean? = true,
)

data class ItemQuantityDao @Default constructor(
  val allocated: ItemQuantityAllocatedDao? = null,
  val available: ItemQuantityAllocatedDao? = null,
  val maximum: ItemQuantityAllocatedDao? = null,
  val minimum: ItemQuantityAllocatedDao? = null,
  val selected: ItemQuantityAllocatedDao? = null
)
