package com.qingzhu.bot.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean

// @Configuration
class FeignConfiguration {
    @Bean
    fun oauth2FeignRequestInterceptor(): RequestInterceptor {
        return FeignOauth2RequestInterceptor()
    }
}