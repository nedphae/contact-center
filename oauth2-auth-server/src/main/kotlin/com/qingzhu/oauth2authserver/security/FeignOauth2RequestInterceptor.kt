package com.qingzhu.oauth2authserver.security

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails

class FeignOauth2RequestInterceptor : RequestInterceptor {
    private val AUTHORIZATION_HEADER = "Authorization"
    private val BEARER_TOKEN_TYPE = "Bearer"
    override fun apply(template: RequestTemplate) {
        val securityContext: SecurityContext = SecurityContextHolder.getContext()
        val authentication: Authentication = securityContext.authentication
        if (authentication.details is OAuth2AuthenticationDetails) {
            val details = authentication.details as OAuth2AuthenticationDetails
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, details.tokenValue))
        }
    }
}