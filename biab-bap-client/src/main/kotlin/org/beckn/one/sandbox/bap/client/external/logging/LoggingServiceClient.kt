package org.beckn.one.sandbox.bap.client.external.logging

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoggingServiceClient {
  @POST("blip")
  fun logging(@Body request: LoggingRequest): Call<LoggingDto>
}