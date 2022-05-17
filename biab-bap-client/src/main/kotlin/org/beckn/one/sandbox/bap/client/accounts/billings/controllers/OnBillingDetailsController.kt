package org.beckn.one.sandbox.bap.client.accounts.billings.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.accounts.billings.services.BillingDetailService
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OnBillingDetailsController @Autowired constructor(
  private val billingService: BillingDetailService,
  private val loggingFactory: LoggingFactory,
  private val loggingService: LoggingService,
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/billing_details")
  @ResponseBody
  fun onBillingDetails(): ResponseEntity<out List<BillingDetailsResponse>> {
    val user = SecurityUtil.getSecuredUserDetail()
    return if (user != null) {
      billingService.findBillingsForCurrentUser(user?.uid!!)
    } else {
      setLogging(null, BppError.AuthenticationError)
      mapToErrorResponse(BppError.AuthenticationError)
    }
  }

  private fun setLogging(context: ProtocolContext?, error: HttpError?) {

    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = ProtocolContext.Action.ON_UPDATE, bppId = context.bppId, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.ON_UPDATE,
        errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message)
    }

    loggingService.postLog(loggerRequest)
  }

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        BillingDetailsResponse(
          userId = null,
          context = null,
          error = it.error(),
          id = null,
          name = null,
          phone = null
        )
      )
    )

}