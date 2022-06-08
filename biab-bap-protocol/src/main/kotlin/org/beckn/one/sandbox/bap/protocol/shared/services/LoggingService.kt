package org.beckn.one.sandbox.bap.protocol.shared.services

import arrow.core.Either
import arrow.core.Either.Right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.registry.RegistryLookupError
import org.beckn.one.sandbox.bap.protocol.external.logging.LoggingDto
import org.beckn.one.sandbox.bap.protocol.external.logging.LoggingRequest
import org.beckn.one.sandbox.bap.protocol.external.logging.LoggingServiceClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
      val jsonRequestBody = jacksonObjectMapper().writeValueAsString(request)
      log.info("Logging request: {}", jsonRequestBody)
      val httpResponse = client.logging(request).execute()
      log.info("Logging response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        httpResponse.isSuccessful -> Right(httpResponse.body()!!)
        else -> Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when logging fails", it)
      RegistryLookupError.Internal
    }
  }
}
