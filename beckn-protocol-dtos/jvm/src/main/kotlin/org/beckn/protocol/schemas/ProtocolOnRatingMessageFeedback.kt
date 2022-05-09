package org.beckn.protocol.schemas

data class ProtocolOnRatingMessageFeedback @Default constructor(
  val id: String? = null,
  val descriptor: String? = null,
  val parentId: String? = null,
)