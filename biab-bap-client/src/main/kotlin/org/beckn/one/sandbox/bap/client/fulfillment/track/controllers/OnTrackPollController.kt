package org.beckn.one.sandbox.bap.client.fulfillment.track.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientTrackResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnTrack
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnTrackPollController(
  onPollService: GenericOnPollService<ProtocolOnTrack, ClientTrackResponse>,
  val contextFactory: ContextFactory,
  val protocolClient: ProtocolClient,
  val loggingFactory: LoggingFactory,
  val loggingService: LoggingService,
) : AbstractOnPollController<ProtocolOnTrack, ClientTrackResponse>(onPollService, contextFactory, loggingFactory, loggingService) {

  @RequestMapping("/client/v1/on_track")
  @ResponseBody
  fun onTrack(@RequestParam messageId: String): ResponseEntity<out ClientResponse> =
    onPoll(messageId, protocolClient.getTrackResponsesCall(messageId), ProtocolContext.Action.ON_TRACK)

  @RequestMapping("/client/v2/on_track")
  @ResponseBody
  fun onTrackV2(@RequestParam messageIds: String): ResponseEntity<out List<ClientResponse>> {
    if (messageIds.isNotEmpty() && messageIds.trim().isNotEmpty()) {
      val messageIdArray = messageIds.split(",")
      var okResponseOnSupport: MutableList<ClientResponse> = ArrayList()

      for (messageId in messageIdArray) {
       val context =  contextFactory.create(messageId = messageId,action= ProtocolContext.Action.ON_TRACK)
        setLogging(context, null)
        val bapResult = onPoll(
          messageId,
          protocolClient.getTrackResponsesCall(messageId),
          ProtocolContext.Action.ON_TRACK
        )
        when (bapResult.statusCode.value()) {
          200 -> {
            val resultResponse = bapResult.body as ClientTrackResponse
            setLogging(resultResponse.context, null)
            okResponseOnSupport.add(resultResponse)
          }
          else -> {
            setLogging(null, bapResult.body?.error)
            okResponseOnSupport.add(
              ClientErrorResponse(
                context = contextFactory.create(messageId = messageId),
                error = bapResult.body?.error
              )
            )
          }
        }
      }
      return ResponseEntity.ok(okResponseOnSupport)
    } else {
      setLogging(null, BppError.BadRequestError.badRequestError)
      return mapToErrorResponse(BppError.BadRequestError)
    }
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext? = null) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientErrorResponse(
          context = context,
          error = it.error()
        )
      )
    )

  private fun setLogging(context: ProtocolContext?, error: ProtocolError?) {
    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = context.action, bppId = context.bppId, errorCode = error?.code,
        errorMessage = error?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.ON_TRACK, errorCode = error?.code,
        errorMessage = error?.message)
    }

    loggingService.postLog(loggerRequest)
  }
}
