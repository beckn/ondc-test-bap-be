package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import okhttp3.OkHttpClient
import org.beckn.one.sandbox.bap.client.external.logging.LoggingServiceClient
import org.beckn.one.sandbox.bap.client.shared.Util
import org.beckn.one.sandbox.bap.client.shared.security.SignRequestInterceptor
import org.beckn.one.sandbox.bap.factories.CircuitBreakerFactory
import org.beckn.one.sandbox.bap.factories.RetryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


@Configuration
class LoggingClientConfiguration(
  @Autowired @Value("\${logging_service.url}")
  private val loggingServiceUrl: String,
  @Value("\${registry_service.retry.max_attempts}")
  private val maxAttempts: Int,
  @Value("\${registry_service.retry.initial_interval_in_millis}")
  private val initialIntervalInMillis: Long,
  @Value("\${registry_service.retry.interval_multiplier}")
  private val intervalMultiplier: Double,
  @Autowired @Value("\${bpp_registry_service.url}")
  private val bppRegistryServiceUrl: String,
  @Value("\${beckn.security.enabled}") private val enableSecurity: Boolean,
  @Value("\${registry_service.timeouts.connection_in_seconds}") private val connectionTimeoutInSeconds: Long,
  @Value("\${registry_service.timeouts.read_in_seconds}") private val readTimeoutInSeconds: Long,
  @Value("\${registry_service.timeouts.write_in_seconds}") private val writeTimeoutInSeconds: Long,

  @Autowired
  private val objectMapper: ObjectMapper,
  @Autowired
  private val interceptor: SignRequestInterceptor
) {

  @Bean
  @Primary
  fun loggingServiceClient(): LoggingServiceClient {
    val url : String = Util.getBaseUri(loggingServiceUrl)
    val retrofit = Retrofit.Builder()
      .baseUrl(url)
      .client(buildHttpClient())
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(getRetryConfig("loggingServiceClient")))
      .build()
    return retrofit.create(LoggingServiceClient::class.java)
  }


  private fun buildHttpClient(): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
      .connectTimeout(connectionTimeoutInSeconds, TimeUnit.SECONDS)
      .readTimeout(readTimeoutInSeconds, TimeUnit.SECONDS)
      .writeTimeout(writeTimeoutInSeconds, TimeUnit.SECONDS)

    return httpClientBuilder.build()
  }

  private fun getRetryConfig(name: String): Retry {
    return RetryFactory.create(
      name,
      maxAttempts,
      initialIntervalInMillis,
      intervalMultiplier
    )
  }

}