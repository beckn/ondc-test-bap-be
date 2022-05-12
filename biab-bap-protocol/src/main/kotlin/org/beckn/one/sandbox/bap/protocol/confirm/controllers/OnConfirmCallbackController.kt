package org.beckn.one.sandbox.bap.protocol.confirm.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractCallbackController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnConfirm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnConfirmCallbackController  @Autowired constructor(
  store: ResponseStorageService<ProtocolOnConfirm>,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
) : AbstractCallbackController<ProtocolOnConfirm>(store, loggingFactory, loggingService) {

  @PostMapping(
    "protocol/v1/on_confirm",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onConfirm(@RequestBody confirmResponse: ProtocolOnConfirm) = onCallback(
    confirmResponse,
    ProtocolContext.Action.ON_CONFIRM
  )

}