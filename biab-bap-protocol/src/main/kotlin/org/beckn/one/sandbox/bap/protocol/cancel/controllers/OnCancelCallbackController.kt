package org.beckn.one.sandbox.bap.protocol.cancel.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractCallbackController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnCancel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnCancelCallbackController  @Autowired constructor(
  store: ResponseStorageService<ProtocolOnCancel>,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
) : AbstractCallbackController<ProtocolOnCancel>(store, loggingFactory, loggingService) {

  @PostMapping(
    "protocol/v1/on_cancel",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onCancel(@RequestBody cancelResponse: ProtocolOnCancel) = onCallback(cancelResponse, ProtocolContext.Action.ON_CANCEL)
}