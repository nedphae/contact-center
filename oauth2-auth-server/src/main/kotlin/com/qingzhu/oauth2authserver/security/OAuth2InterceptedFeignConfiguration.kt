package com.qingzhu.oauth2authserver.security

import feign.RequestInterceptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext


@ConditionalOnClass(LoadBalancedResourceDetails::class, LoadBalancerClient::class)
class OAuth2InterceptedFeignConfiguration {

    @Bean("oauth2RequestInterceptor")
    @ConditionalOnMissingBean
    fun getOAuth2RequestInterceptor(loadBalancedResourceDetails: LoadBalancedResourceDetails): RequestInterceptor {
        return OAuth2FeignRequestInterceptor(DefaultOAuth2ClientContext(), loadBalancedResourceDetails)
    }
}