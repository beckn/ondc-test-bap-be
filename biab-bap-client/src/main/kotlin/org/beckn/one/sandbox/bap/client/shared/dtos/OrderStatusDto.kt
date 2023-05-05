package org.beckn.one.sandbox.bap.client.shared.dtos

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.Default
import org.beckn.protocol.schemas.ProtocolOrderStatusRequestMessage

data class OrderStatusDto @Default constructor(
  val context: ClientContext,
  val message: ProtocolOrderStatusRequestMessage,
  val orderObject: ProtocolOrderObject
) {
  fun validate(): Either<BppError, OrderStatusDto> =
    when (context.bppId) {
      null -> BppError.BppIdNotPresent.left()
      else -> this.right()
    }
}

data class ProtocolOrderObject @Default constructor(
  val context: ClientContext,
  val message: Message
  ){}

data class Message @Default constructor(
  val order: OrderObject?
)

data class OrderObject @Default constructor(
  val id: String,
  val created_at: String,
  val billing: Billing,
  val item: List<Item>
)

data class Billing @Default constructor(
  val address: Address
)

data class Address @Default constructor(
  val street: String
)

data class Item @Default constructor(
  val descriptor: Descriptor,
  val price: Price,
  val quantity: Int,
  val tags: Tags,
  val fulfillment: Fulfillment
)

data class Descriptor @Default constructor(
  val name: String,
  val images: List<String>,
  val short_desc: String
)

data class Price @Default constructor(
  val value: String
)

data class Tags @Default constructor(
  val fulfillment_end_loc: String,
  val fulfillment_end_time: String,
  val fulfillment_start_loc: String,
  val fulfillment_start_time: String
)

data class Fulfillment @Default constructor(
  val start: Start,
  val end: End
)

data class Start @Default constructor(
  val location: Location
)

data class End @Default constructor(
  val location: Location
)

data class Location @Default constructor(
  val gps: String
)