package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import org.beckn.one.sandbox.bap.protocol.shared.security.CrypticKeyStore
import org.beckn.one.sandbox.bap.protocol.shared.security.RequestWrappingFilter
import org.beckn.one.sandbox.bap.protocol.shared.security.SignatureVerificationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class SecurityConfiguration(
  @Autowired val keyStores: List<CrypticKeyStore>,
  @Autowired val env: Environment,
  @Autowired val objectMapper: ObjectMapper
) : WebMvcConfigurer {

  override fun addInterceptors(registry: InterceptorRegistry) {
    if (env.getProperty("beckn.security.enabled", Boolean::class.java, true)) {
      val proxyAuthInterceptor = SignatureVerificationInterceptor(
        keyStores, listOf("Proxy-Authorization", "Authorization"), objectMapper
      )
      val authInterceptor = SignatureVerificationInterceptor(
        keyStores, listOf("Authorization"),objectMapper
      )
//      registry.addInterceptor(proxyAuthInterceptor)
//        .addPathPatterns("/protocol/v1/on_search")

      registry.addInterceptor(authInterceptor)
//        .excludePathPatterns("/protocol/v1/on_search")
        .excludePathPatterns("/protocol/response/**")
    }
  }

  @Bean
  @ConditionalOnProperty(value = ["beckn.security.enabled"], havingValue = "true", matchIfMissing = false)
  fun requestWrappingFilter(): FilterRegistrationBean<RequestWrappingFilter>? {
    val registrationBean: FilterRegistrationBean<RequestWrappingFilter> =
      FilterRegistrationBean<RequestWrappingFilter>()
    registrationBean.filter = RequestWrappingFilter()
    registrationBean.addUrlPatterns("/protocol/v1/*")
    registrationBean.order = Ordered.HIGHEST_PRECEDENCE
    return registrationBean
  }
}