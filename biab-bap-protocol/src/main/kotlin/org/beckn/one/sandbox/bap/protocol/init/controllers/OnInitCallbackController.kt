package org.beckn.one.sandbox.bap.protocol.init.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractCallbackController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnInit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class OnInitCallbackController  @Autowired constructor(
  store: ResponseStorageService<ProtocolOnInit>,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
): AbstractCallbackController<ProtocolOnInit>(store, loggingFactory, loggingService) {

  @PostMapping(
    "protocol/v1/on_init",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onInit(@RequestBody initResponse: ProtocolOnInit) = onCallback(initResponse, ProtocolContext.Action.ON_INIT)

}