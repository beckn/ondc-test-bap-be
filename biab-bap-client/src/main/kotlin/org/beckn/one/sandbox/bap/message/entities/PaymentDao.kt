package org.beckn.one.sandbox.bap.message.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.protocol.schemas.Default

data class PaymentDao  @Default constructor(
  val uri: java.net.URI? = null,
  val tlMethod: TlMethod? = null,
  val params: Map<String, String>? = null,
  val type: Type? = null,
  val status: Status? = null,
  val time: TimeDao? = null,
  val collectedBy: CollectedBy? = null,
  val ondcCollectedByStatus: CollectedByStatus? = null,
  val ondcBuyerAppFinderFeeType: BuyerAppFinderFeeType? = null,
  val ondcBuyerAppFinderFeeAmount: String? = null,
  val ondcWithHoldingAmount: String? = null,
  val ondcWithHoldingAmountStatus: CollectedByStatus? = null,
  val ondcReturnWindow: String? = null,
  val ondcReturnWindowStatus: CollectedByStatus? = null,
  val ondcSettlementBasis: String? = null,
  val ondcSettlementBasisStatus:CollectedByStatus? = null,
  val ondcSettlementWindow: String? = null,
  val ondcSettlementWindowStatus: CollectedByStatus? = null,
  val ondcSettlementDetails: List<SettlementDetailsDao>? = null,
) {

  /**
   *
   * Values: get,post
   */
  enum class TlMethod(val value: String) {
    @JsonProperty("http/get") GET("http/get"),
    @JsonProperty("http/post") POST("http/post");
  }
  /**
   *
   * Values: oNMinusORDER,pREMinusFULFILLMENT,oNMinusFULFILLMENT,pOSTMinusFULFILLMENT
   */
  enum class Type(val value: String) {
    @JsonProperty("ON-ORDER") ONORDER("ON-ORDER"),
    @JsonProperty("PRE-FULFILLMENT")  PREFULFILLMENT("PRE-FULFILLMENT"),
    @JsonProperty("ON-FULFILLMENT")  ONFULFILLMENT("ON-FULFILLMENT"),
    @JsonProperty("POST-FULFILLMENT")  POSTFULFILLMENT("POST-FULFILLMENT");
  }
  /**
   *
   * Values: pAID,nOTMinusPATD
   */
  enum class Status(val value: String) {
    PAID("PAID"),
    @JsonProperty("NOT-PAID") NOTPAID("NOT-PAID");
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

data class SettlementDetailsDao @Default constructor(
  val settlementCounterParty: SettlementCounterParty? = null,
  val settlementPhase: SettlementPhase? = null,
  val settlementType: SettlementType? = null,
  val settlementBankAccountNo: String? = null,
  val settlementIfscCode: String? = null,
  val upiAddress: String? = null,
  val settlementStatus: SettlementStatus? = null,
  val settlementReference: String? = null,
  val settlementTimestamp: java.time.OffsetDateTime? = null,
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