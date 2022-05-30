package org.beckn.one.sandbox.bap.client.discovery.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.LoggingService
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnSearch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnSearchPollController @Autowired constructor(
  onPollService: GenericOnPollService<ProtocolOnSearch, ClientSearchResponse>,
  contextFactory: ContextFactory,
  val protocolClient: ProtocolClient,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService,
) : AbstractOnPollController<ProtocolOnSearch, ClientSearchResponse>(onPollService, contextFactory, loggingFactory, loggingService) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/on_search")
  @ResponseBody
  fun onSearchV1(@RequestParam messageId: String,
                 @RequestParam providerName : String?,
                 @RequestParam categoryName: String?): ResponseEntity<out ClientResponse> {
    log.info("on Search for client layer : $messageId, $providerName, $categoryName")
   return onPoll(
      messageId,
      protocolClient.getSearchResponsesCall(messageId, providerName, categoryName), ProtocolContext.Action.ON_SEARCH
    )
  }
}