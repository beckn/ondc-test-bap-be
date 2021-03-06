package org.beckn.protocol.schemas

import com.fasterxml.jackson.annotation.JsonProperty

data class ProtocolPayment @Default constructor(
  val uri: java.net.URI? = null,
  val tlMethod: TlMethod? = null,
  val params: Map<String, String>? = null,
  val type: Type? = null,
  val status: Status? = null,
  val time: ProtocolTime? = null,
  val collectedBy: CollectedBy? = null,
  @JsonProperty("@ondc/org/collected_by_status") val collectedByStatus: CollectedByStatus? = null,
  @JsonProperty("@ondc/org/buyer_app_finder_fee_type") val buyerAppFinderFeeType: BuyerAppFinderFeeType? = null,
  @JsonProperty("@ondc/org/buyer_app_finder_fee_amount") val buyerAppFinderFeeAmount: String? = null,
  @JsonProperty("@ondc/org/withholding_amount") val withHoldingAmount: String? = null,
  @JsonProperty("@ondc/org/withholding_amount_status") val withHoldingAmountStatus: CollectedByStatus? = null,
  @JsonProperty("@ondc/org/return_window") val returnWindow: String? = null,
  @JsonProperty("@ondc/org/return_window_status") val returnWindowStatus: CollectedByStatus? = null,
  @JsonProperty("@ondc/org/settlement_basis") val settlementBasis: String? = null,
  @JsonProperty("@ondc/org/settlement_basis_status") val settlementBasisStatus: CollectedByStatus? = null,
  @JsonProperty("@ondc/org/settlement_window") val settlementWindow: String? = null,
  @JsonProperty("@ondc/org/settlement_window_status") val settlementWindowStatus: CollectedByStatus? = null,
  @JsonProperty("@ondc/org/settlement_details") val settlementDetails: List<SettlementDetails>? = null,

) {

  /**
   *
   * Values: get,post
   */
  enum class TlMethod(val value: String) {
    @JsonProperty("http/get")
    GET("http/get"),
    @JsonProperty("http/post")
    POST("http/post");
  }

  /**
   *
   * Values: oNMinusORDER,pREMinusFULFILLMENT,oNMinusFULFILLMENT,pOSTMinusFULFILLMENT
   */
  enum class Type(val value: String) {
    @JsonProperty("ON-ORDER")
    ONORDER("ON-ORDER"),
    @JsonProperty("PRE-FULFILLMENT")
    PREFULFILLMENT("PRE-FULFILLMENT"),
    @JsonProperty("ON-FULFILLMENT")
    ONFULFILLMENT("ON-FULFILLMENT"),
    @JsonProperty("POST-FULFILLMENT")
    POSTFULFILLMENT("POST-FULFILLMENT");
  }

  enum class Status(val value: String) {
    PAID("PAID"),
    @JsonProperty("NOT-PAID")
    NOTPAID("NOT-PAID");
  }

  enum class CollectedBy(val value: String) {
    BAP("BAP"),
    BPP("BPP");
  }

  enum class CollectedByStatus(val value: String) {
    @JsonProperty("Assert")
    ASSERT("Assert"),
    @JsonProperty("Agree")
    AGREE("Agree"),
    @JsonProperty("Disagree")
    DISAGREE("Disagree"),
    @JsonProperty("Terminate")
    TERMINATE("Terminate"),
  }

  enum class BuyerAppFinderFeeType(val value: String) {
    @JsonProperty("Amount")
    AMOUNT("Amount"),
    @JsonProperty("Percent")
    PERCENT("Percent"),
  }
}

data class PaymentParams @Default constructor(
  val transaction_id: String? = null,
  val transaction_status: String? = null,
  val amount: String? = null,
  val currency: ProtocolPrice? = null,
)

data class SettlementDetails @Default constructor(
  @JsonProperty("settlement_counterparty") val settlementCounterParty: SettlementCounterParty? = null,
  @JsonProperty("settlement_phase") val settlementPhase: SettlementPhase? = null,
  @JsonProperty("settlement_type") val settlementType: SettlementType? = null,
  @JsonProperty("settlement_bank_account_no") val settlementBankAccountNo: String? = null,
  @JsonProperty("settlement_ifsc_code") val settlementIfscCode: String? = null,
  @JsonProperty("upi_address") val upiAddress: String? = null,
  @JsonProperty("settlement_status") val settlementStatus: SettlementStatus? = null,
  @JsonProperty("settlement_reference") val settlementReference: String? = null,
  @JsonProperty("settlement_timestamp") val settlementTimestamp: java.time.OffsetDateTime? = null,
) {

  enum class SettlementCounterParty(val value: String) {
    @JsonProperty("buyer-app")
    BUYERAPP("buyer-app"),
    @JsonProperty("seller-app")
    SELLERAPP("seller-app"),
    @JsonProperty("logistics-provider")
    LOGISTICSPROVIDER("logistics-provider"),
  }

  enum class SettlementPhase(val value: String) {
    @JsonProperty("sale-amount")
    SALEAMOUNT("sale-amount"),
    @JsonProperty("withholding-amount")
    WITHHOLDINGAMOUNT("withholding-amount"),
  }

  enum class SettlementType(val value: String) {
    @JsonProperty("neft")
    NEFT("neft"),
    @JsonProperty("rtgs")
    RTGS("rtgs"),
    @JsonProperty("upi")
    UPI("upi"),
  }

  enum class SettlementStatus(val value: String) {
    @JsonProperty("PAID")
    PAID("PAID"),
    @JsonProperty("NOT-PAID")
    NOTPAID("NOT-PAID"),
  }
}