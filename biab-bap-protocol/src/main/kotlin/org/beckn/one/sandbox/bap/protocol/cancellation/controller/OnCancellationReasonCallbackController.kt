package org.beckn.one.sandbox.bap.protocol.cancellation.controller

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractCallbackController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolOnCancel
import org.beckn.protocol.schemas.ProtocolOnCancellationReasons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnCancellationReasonCallbackController  @Autowired constructor(
  store: ResponseStorageService<ProtocolOnCancellationReasons>,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
) : AbstractCallbackController<ProtocolOnCancellationReasons>(store, loggingFactory, loggingService) {


  @PostMapping(
    "protocol/v1/cancellation_reasons",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onCancellationReasons(@RequestBody cancellationReasonsRequest: ProtocolOnCancellationReasons) = onCallback(cancellationReasonsRequest)

}