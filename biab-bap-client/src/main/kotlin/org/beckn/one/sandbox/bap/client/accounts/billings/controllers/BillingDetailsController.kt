package org.beckn.one.sandbox.bap.client.accounts.billings.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.*
import org.litote.kmongo.newId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BillingDetailsController @Autowired constructor(
  private val responseStorageService: ResponseStorageService<BillingDetailsResponse,BillingDetailsDao>,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/billing_details")
  @ResponseBody
  fun deliveryAddress(@RequestBody request: BillingDetailRequestDto): ResponseEntity<BillingDetailsResponse> {
    val user = SecurityUtil.getSecuredUserDetail()
    if (user == null) {
      setLogging(null, BppError.AuthenticationError )
     return  mapToErrorResponse(BppError.AuthenticationError)
    } else {
      val billingDao = BillingDetailsDao(
        userId = user?.uid,
        id = newId<String>().toString(),
        address = request.address,
        organization = request.organization,
        locationId = request.locationId,
        email = request.email,
        phone = request.phone,
        taxNumber = request.taxNumber,
        name = request.name,
      )
      return responseStorageService
        .save(billingDao)
        .fold(
          {
            log.error("Error when saving billing response by user Id. Error: {}", it)
            setLogging(null, it )

            ResponseEntity
              .status(it.status())
              .body(
                BillingDetailsResponse(
                  id = null,
                  context = null, name = null, phone = null,
                  error = ProtocolError(
                    code = it.status().name,
                    message = it.message().toString()
                  ), userId = null
                )
              )
          },
          {
            log.info("Saved Billing Info of User {}")
            setLogging(it.context, null )
            ResponseEntity.ok(it)
          }
        )
    }
  }


  private fun setLogging(context: ProtocolContext?, error: HttpError?) {

    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = ProtocolContext.Action.UPDATE, bppId = context.bppId, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.UPDATE,
        errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message)
    }

    loggingService.postLog(loggerRequest)
  }

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
        BillingDetailsResponse(
          userId = null,
          context = null,
          error = it.error(),
          id = null,
          name = null,
          phone = null
        )
    )

}