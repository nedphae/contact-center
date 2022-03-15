package com.qingzhu.oauth2authserver.config

import feign.RequestInterceptor
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails

@Configuration
class FeignConfiguration {
    @Bean
    fun oauth2FeignRequestInterceptor(
        @Qualifier("oauth2ClientContext") context: OAuth2ClientContext,
        resource: ObjectProvider<OAuth2ProtectedResourceDetails>
    ): RequestInterceptor {
        return OAuth2FeignRequestInterceptor(context, resource.ifAvailable)
    }
}