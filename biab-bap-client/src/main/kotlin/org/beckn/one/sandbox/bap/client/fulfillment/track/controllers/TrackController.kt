package org.beckn.one.sandbox.bap.client.fulfillment.track.controllers

import org.beckn.one.sandbox.bap.client.fulfillment.track.services.TrackService
import org.beckn.one.sandbox.bap.client.shared.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TrackController @Autowired constructor(
  val contextFactory: ContextFactory,
  val trackService: TrackService,
  val loggingService: LoggingService,
  val loggingFactory: LoggingFactory
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/track")
  @ResponseBody
  fun track(@RequestBody request: TrackRequestDto): ResponseEntity<ProtocolAckResponse> {
    val context = contextFactory.create(action = ProtocolContext.Action.TRACK)
    return trackService.track(context, request)
      .fold(
        {
          log.error("Error when getting tracking information: {}", it)
          setLogging(context, it)
          mapToErrorResponse(it, context)
        },
        {
          log.info("Successfully initiated track api. Message: {}", it)
          setLogging(context, null)
          ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
        }
      )
  }

  @RequestMapping("/client/v2/track")
  @ResponseBody
  fun trackV2(@RequestBody trackRequestList: List<TrackRequestDto>): ResponseEntity<List<ProtocolAckResponse>> {

    if(!trackRequestList.isNullOrEmpty()){
      var okResponseTrack : MutableList<ProtocolAckResponse> = ArrayList()
      for (trackRequest in trackRequestList) {
        val context = contextFactory.create(action = ProtocolContext.Action.TRACK)
        trackService.track(context, trackRequest)
          .fold(
            {
              log.error("Error when getting tracking information: {}", it)
              setLogging(context, it)
              okResponseTrack.add( ProtocolAckResponse(
                context = context,
                message = it.message(),
                error = it.error()
              ))
            },
            {
              log.info("Successfully initiated track api. Message: {}", it)
              setLogging(context, null)
              okResponseTrack.add(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
            }
          )
      }
      return ResponseEntity.ok(okResponseTrack)
    }else{
      setLogging(null, BppError.BadRequestError)

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
          listOf(
            ProtocolAckResponse(
              context = null, message = BppError.BadRequestError.message(),
              error = BppError.BadRequestError.error()
            )
          )
        )
    }
  }

  private fun setLogging(context: ProtocolContext?, error: HttpError?) {
    val loggerRequest = if(context != null) {
      loggingFactory.create(messageId = context.messageId,
        transactionId = context.transactionId, contextTimestamp = context.timestamp.toString(),
        action = context.action, bppId = context.bppId, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message
      )
    } else {
      loggingFactory.create(action = ProtocolContext.Action.TRACK, errorCode = error?.error()?.code,
        errorMessage = error?.error()?.message)
    }

    loggingService.postLog(loggerRequest)
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext? = null) = ResponseEntity
    .status(it.status())
    .body(ProtocolAckResponse(context = context, message = it.message(), error = it.error()))

}