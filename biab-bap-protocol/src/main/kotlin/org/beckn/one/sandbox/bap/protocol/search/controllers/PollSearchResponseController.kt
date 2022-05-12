package org.beckn.one.sandbox.bap.protocol.search.controllers

import org.beckn.one.sandbox.bap.protocol.shared.controllers.AbstractPollForResponseController
import org.beckn.one.sandbox.bap.protocol.shared.services.LoggingService
import org.beckn.one.sandbox.bap.protocol.shared.services.PollForResponseService
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.LoggingFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PollSearchResponseController @Autowired constructor(
  responseService: PollForResponseService<ProtocolOnSearch>,
  contextFactory: ContextFactory,
  loggingFactory: LoggingFactory,
  loggingService: LoggingService
): AbstractPollForResponseController<ProtocolOnSearch>(responseService, contextFactory, loggingFactory, loggingService) {

  @GetMapping("protocol/response/v1/on_search")
  @ResponseBody
  fun getSearchResponses(messageId: String) = findResponses(messageId, ProtocolContext.Action.ON_SEARCH)

}