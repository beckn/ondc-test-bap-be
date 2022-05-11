package org.beckn.one.sandbox.bap.protocol.search.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractCallbackController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolOnSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnSearchCallbackController @Autowired constructor(
  store: ResponseStorageService<ProtocolOnSearch>,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
): AbstractCallbackController<ProtocolOnSearch>(store, loggingFactory, loggingService) {

  @PostMapping(
    "protocol/v1/on_search",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onSearch(@RequestBody searchResponse: ProtocolOnSearch) = onCallback(searchResponse)

}