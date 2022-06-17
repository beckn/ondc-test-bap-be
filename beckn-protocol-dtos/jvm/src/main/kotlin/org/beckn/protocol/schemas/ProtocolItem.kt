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
  @JsonProperty("@ondc/org/returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("@ondc/org/cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("@ondc/org/seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("@ondc/org/return_window") val ondcReturnWindow: String?,
  @JsonProperty("@ondc/org/time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("@ondc/org/available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("@ondc/org/statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  @JsonProperty("@ondc/org/statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
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
  @JsonProperty("@ondc/org/importer_address") val importerAddress: String? = null,
  @JsonProperty("@ondc/org/importer_FSSAI_license_no") val importerFssaiLicenseNo: String? = null,
  @JsonProperty("@ondc/org/importer_FSSAI_logo") val importerFssaiLogo: String? = null,
  @JsonProperty("@ondc/org/importer_name") val importerName: String? = null,
  @JsonProperty("@ondc/org/ingredients_info") val ingredientsInfo: String? = null,
  @JsonProperty("@ondc/org/manufacturer_or_packer_address") val ManufacturerPackerAddress: String? = null,
  @JsonProperty("@ondc/org/manufacturer_or_packer_name") val ManufacturerPackerName: String? = null,
  @JsonProperty("@ondc/org/net_quantity") val netQuantity: String? = null,
  @JsonProperty("@ondc/org/nutritional_info") val nutritionalInfo: String? = null,
  @JsonProperty("@ondc/org/other_FSSAI_license_no") val otherFssaiLicenseNo: String? = null,
  @JsonProperty("@ondc/org/other_importer_address") val otherImporterAddress: String? = null,
  @JsonProperty("@ondc/org/other_importer_country_of_origin") val otherImporterCountryOrigin: String? = null,
  @JsonProperty("@ondc/org/other_importer_name") val otherImporterName: String? = null,
  @JsonProperty("@ondc/org/other_premises") val otherPremises: String? = null,
)