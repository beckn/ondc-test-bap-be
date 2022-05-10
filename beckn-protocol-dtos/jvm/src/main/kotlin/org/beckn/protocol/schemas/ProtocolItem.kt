package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolItem @Default constructor(
  val id: String? = null,
  val parentItemId: String? = null,
  val fulfillmentId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: ProtocolTime? = null,
  val matched: Boolean?,
  val related: Boolean?,
  val recommended: Boolean?,
  val tags: Map<String, String>? = null,
  val rating: Int? = null,
  val ondcReturnable: Boolean? = true,
  val ondcCancellable: Boolean? = true,
  val ondcSellerPickupReturn: Boolean? = true,
  val ondcReturnWindow: String?,
  val ondcTimeToShip: String? = null,
  val ondcAvailableOnCod: Boolean? = true,
  val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
  )

 data class OndcStatutoryPackagedCommodities @Default constructor(
   val commonOrGenericNameOfCommodity: String? = null,
   val contactDetailsConsumerCare: String? = null,
   val importedProductCountryOfOrigin: String? = null,
   val manufacturerOrPackerAddress: String? = null,
   val manufacturerOrPackerName: String? = null,
   val monthYearOfManufacturePackingImport: String? = null,
   val multipleProductsNameNumberOrQty: String? = null,
   val netQuantityOrMeasureOfCommodityInPkg: String? = null,
 )

data class OndcStatutoryPackagedFood @Default constructor(
  val additivesInfo: String? = null,
  val brandOwnerAddress: String? = null,
  val brandOwnerFssaiLicenseNo: String? = null,
  val brandOwnerFssaiLogo: String? = null,
  val brandOwnerName: String? = null,
  val contactDetailsConsumerCare: String? = null,
  val importterProductCountryOrigin: String? = null,
  val importerAddress: String? = null,
  val importerFssaiLicenseNo: String? = null,
  val importerFssaiLogo: String? = null,
  val importerName: String? = null,
  val ingredientsInfo: String? = null,
  val ManufacturerPackerAddress: String? = null,
  val ManufacturerPackerName: String? = null,
  val netQuantity: String? = null,
  val nutritionalInfo: String? = null,
  val otherFssaiLicenseNo: String? = null,
  val otherImporterAddress: String? = null,
  val otherImporterCountryOrigin: String? = null,
  val otherImporterName: String? = null,
  val otherPremises: String? = null,
)