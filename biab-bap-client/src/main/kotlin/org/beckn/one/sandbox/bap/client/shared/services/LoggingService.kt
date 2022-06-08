package org.beckn.one.sandbox.bap.client.shared.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.logging.LoggingDto
import org.beckn.one.sandbox.bap.client.external.logging.LoggingRequest
import org.beckn.one.sandbox.bap.client.external.logging.LoggingServiceClient
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.errors.registry.RegistryLookupError.Internal
import org.beckn.one.sandbox.bap.errors.HttpError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class LoggingService(
  @Autowired private val loggingServiceClient: LoggingServiceClient
) {
  private val log: Logger = LoggerFactory.getLogger(RegistryService::class.java)

  fun postLog(request: LoggingRequest): Either<HttpError, LoggingDto> {
    return logging(loggingServiceClient, request)
  }

  private fun logging(
    client: LoggingServiceClient,
    request: LoggingRequest
  ): Either<HttpError, LoggingDto> {
    return Either.catch {
     val json = jacksonObjectMapper().writeValueAsString(request)
      log.info("Logging request: {}", json)
      val httpResponse = client.logging(request).execute()
      log.info("Logging response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
        return when {
        httpResponse.isInternalServerError() -> Left(Internal)
        noLoggingFound(httpResponse) -> Left(BppError.NullResponse)
        else -> Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when logging", it)
      Internal
    }
  }

  private fun noLoggingFound(httpResponse: Response<LoggingDto>) =
    httpResponse.body() == null
}
