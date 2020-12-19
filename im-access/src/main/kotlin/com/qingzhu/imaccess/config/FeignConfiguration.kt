package com.qingzhu.imaccess.config

import com.qingzhu.common.security.FeignOauth2RequestInterceptor
import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfiguration {
    @Bean
    fun oauth2FeignRequestInterceptor(): RequestInterceptor {
        return FeignOauth2RequestInterceptor()
    }
}