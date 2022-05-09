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
  @JsonProperty("./ondc-returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("./ondc-cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("./ondc-seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("./ondc-return_window") val ondcReturnWindow: String?,
  @JsonProperty("./ondc-time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("./ondc-available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("./ondc-statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommodities?,
  @JsonProperty("./ondc-statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFood?,
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
  @JsonProperty("additives_info") val additivesInfo: String? = null,
  @JsonProperty("brand_owner_address") val brandOwnerAddress: String? = null,
  @JsonProperty("brand_owner_FSSAI_license_no") val brandOwnerFssaiLicenseNo: String? = null,
  @JsonProperty("brand_owner_FSSAI_logo") val brandOwnerFssaiLogo: String? = null,
  @JsonProperty("brand_owner_name") val brandOwnerName: String? = null,
  @JsonProperty("contact_details_consumer_care") val contactDetailsConsumerCare: String? = null,
  @JsonProperty("imported_product_country_of_origin") val importterProductCountryOrigin: String? = null,
  @JsonProperty("importer_address") val importerAddress: String? = null,
  @JsonProperty("importer_FSSAI_license_no") val importerFssaiLicenseNo: String? = null,
  @JsonProperty("importer_FSSAI_logo") val importerFssaiLogo: String? = null,
  @JsonProperty("importer_name") val importerName: String? = null,
  @JsonProperty("ingredients_info") val ingredientsInfo: String? = null,
  @JsonProperty("manufacturer_or_packer_address") val ManufacturerPackerAddress: String? = null,
  @JsonProperty("manufacturer_or_packer_name") val ManufacturerPackerName: String? = null,
  @JsonProperty("net_quantity") val netQuantity: String? = null,
  @JsonProperty("nutritional_info") val nutritionalInfo: String? = null,
  @JsonProperty("other_FSSAI_license_no") val otherFssaiLicenseNo: String? = null,
  @JsonProperty("other_importer_address") val otherImporterAddress: String? = null,
  @JsonProperty("other_importer_country_of_origin") val otherImporterCountryOrigin: String? = null,
  @JsonProperty("other_importer_name") val otherImporterName: String? = null,
  @JsonProperty("other_premises") val otherPremises: String? = null,
)