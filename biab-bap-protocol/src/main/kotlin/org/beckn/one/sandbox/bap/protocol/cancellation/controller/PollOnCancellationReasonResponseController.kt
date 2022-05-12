package org.beckn.one.sandbox.bap.protocol.cancellation.controller

import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractPollForResponseController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.protocol.shared.services.PollForResponseService
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnCancellationReasons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PollOnCancellationReasonResponseController @Autowired constructor(
  responseService: PollForResponseService<ProtocolOnCancellationReasons>,
  contextFactory: ContextFactory,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
) : AbstractPollForResponseController<ProtocolOnCancellationReasons>(responseService, contextFactory, loggingFactory, loggingService) {

  @GetMapping("protocol/response/v1/on_cancellation_reasons")
  @ResponseBody
  fun getCancellationReasonsResponses(messageId: String) = findResponses(messageId, ProtocolContext.Action.ON_CANCEL)
}