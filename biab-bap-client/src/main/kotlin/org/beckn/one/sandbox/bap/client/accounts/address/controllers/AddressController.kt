package org.beckn.one.sandbox.bap.client.accounts.address.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.accounts.address.services.AddressServices
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AddressController @Autowired constructor(
  private val addressServices: AddressServices,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,
  val contextFactory: ContextFactory,

  ) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/delivery_address")
  @ResponseBody
  fun deliveryAddress(@RequestBody request: DeliveryAddressRequestDto): ResponseEntity<DeliveryAddressResponse> {
    val user = SecurityUtil.getSecuredUserDetail()
    if (user == null) {
      setLogging(context = null , error = BppError.AuthenticationError)
      return mapToErrorResponse(BppError.AuthenticationError)
    } else {
      return addressServices
        .updateAndSaveAddress(request)
        .fold(
          {
            setLogging(context = null , error = it)
            log.error("Error when saving address response by user id. Error: {}", it)
            ResponseEntity
              .status(it.status())
              .body(
                DeliveryAddressResponse(
                  id = null, userId = null, context = null,
                  error = ProtocolError(code = it.status().name, message = it.message().toString())
                )
              )
          },
          {
            log.info("Saved address details of user {}")
            setLogging(context = it.context, error= null)
            ResponseEntity.ok(it)
          }
        )
    }
  }

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      DeliveryAddressResponse(
        userId = null,
        context = null,
        error = it.error(),
        id = null
      )
    )

  private fun setLogging(context: ProtocolContext?, error: HttpError?) {

    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = ProtocolContext.Action.UPDATE, bppId = context.bppId, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.UPDATE, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message)
    }

    loggingService.postLog(loggerRequest)
  }
}