package org.beckn.protocol.schemas

import java.time.LocalDateTime

interface Category{
  val id: String?
  val parentCategoryId: String?
  val descriptor: ProtocolDescriptor
  val time: LocalDateTime?
  val tags: Map<String, String>?
}

data class ProtocolCategory @Default constructor(
  override val id: String? = null,
  override val parentCategoryId: String? = null,
  override val descriptor: ProtocolDescriptor,
  override val time: LocalDateTime? = null,
  override val tags: Map<String, String>? = null
) : Category

data class ProtocolRatingCategory @Default constructor(
  override val id: String,
  override val parentCategoryId: String? = null,
  override val descriptor: ProtocolDescriptor,
  override val time: LocalDateTime? = null,
  override val tags: Map<String, String>? = null,
  val question: String? = null
) : Category