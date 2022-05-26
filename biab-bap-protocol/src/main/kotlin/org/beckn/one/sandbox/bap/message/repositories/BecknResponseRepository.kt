package org.beckn.one.sandbox.bap.message.repositories

import arrow.core.andThen
import arrow.optics.cons
import com.mongodb.client.MongoCollection
import org.beckn.one.sandbox.bap.message.entities.*
import org.litote.kmongo.*

open class BecknResponseRepository<R : BecknResponseDao>(
  collection: MongoCollection<R>
) : GenericRepository<R>(collection) {

  fun findByMessageId(id: String): List<R> = findAll(BecknResponseDao::context / ContextDao::messageId eq id)
  fun findByOrderId(id: String) : List<R> = findAll(OnOrderStatusDao::message / OnOrderStatusMessageDao::order / OrderDao::id eq id)
  fun findByCategoryName(id: String, categoryName: String) : List<R> = findAll(BecknResponseDao::context / ContextDao::messageId eq id)
  fun findByProviderName(id: String, providerName: String) : List<R> = findAll(and(OnSearchDao::context / ContextDao::messageId eq id, OnSearchDao::message/ OnSearchMessageDao::catalog / CatalogDao::bppDescriptor/ DescriptorDao::name  regex providerName))

}