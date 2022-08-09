package org.beckn.one.sandbox.bap.client.shared.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.registry.RegistryClient
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.shared.errors.registry.RegistryLookupError
import org.beckn.one.sandbox.bap.client.shared.errors.registry.RegistryLookupError.Internal
import org.beckn.one.sandbox.bap.client.shared.errors.registry.RegistryLookupError.NoSubscriberFound
import org.beckn.one.sandbox.bap.configurations.RegistryClientConfiguration.Companion.BPP_REGISTRY_SERVICE_CLIENT
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import retrofit2.Response

object CacheName {
  const val gateways = "gateways"
  const val bppsById = "bppsById"
}

@Service
class RegistryService(
  @Autowired private val cacheManager: CacheManager,
  @Autowired private val registryServiceClient: RegistryClient,
  @Qualifier(BPP_REGISTRY_SERVICE_CLIENT) @Autowired private val bppRegistryServiceClient: RegistryClient,
  @Value("\${context.domain}") private val domain: String,
  @Value("\${context.city}") private val city: String,
  @Value("\${context.country}") private val country: String
) {
  private val log: Logger = LoggerFactory.getLogger(RegistryService::class.java)

  @Cacheable(CacheName.gateways)
  fun lookupGateways(): Either<RegistryLookupError, List<SubscriberDto>> {
    return lookup(registryServiceClient, lookupRequest(subscriberType = Subscriber.Type.BG))
  }

  @Cacheable(CacheName.bppsById)
  fun lookupBppById(id: String): Either<RegistryLookupError, List<SubscriberDto>> {
    return lookup(bppRegistryServiceClient, lookupRequest(subscriberType = Subscriber.Type.BPP, subscriberId = id))
  }

  @Scheduled(cron = "\${registry_service.cache.expiry_cron_schedule}")
  fun clearGatewayCache() {
    log.info("Clearing Gateways Cache")
    cacheManager.getCache(CacheName.gateways)?.clear()
  }

  @Scheduled(cron = "\${registry_service.cache.expiry_cron_schedule}")
  fun clearBppsByIdCache() {
    log.info("Clearing BPPs by ID Cache")
    cacheManager.getCache(CacheName.bppsById)?.clear()
  }

  private fun lookup(
    client: RegistryClient,
    request: SubscriberLookupRequest
  ): Either<RegistryLookupError, List<SubscriberDto>> {
    return Either.catch {
      log.info("Looking up subscribers: {}", request)
      val httpResponse = client.lookup(request).execute()
      log.info("Lookup subscriber response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        httpResponse.isInternalServerError() -> Left(Internal)
        noSubscribersFound(httpResponse) -> Left(NoSubscriberFound)
        else -> Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when looking up subscribers", it)
      Internal
    }
  }

  private fun lookupRequest(subscriberType: Subscriber.Type, subscriberId: String? = null) = SubscriberLookupRequest(
    subscriber_id = subscriberId,
    type = subscriberType,
    domain = domain,
    city = if (subscriberType === Subscriber.Type.BPP) null else city,
    country = country
  )

  private fun noSubscribersFound(httpResponse: Response<List<SubscriberDto>>) =
    httpResponse.body() == null || httpResponse.body()?.isEmpty() == true
}
