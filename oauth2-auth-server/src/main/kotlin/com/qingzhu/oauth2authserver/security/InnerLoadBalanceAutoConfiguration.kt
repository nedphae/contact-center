package com.qingzhu.oauth2authserver.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails


@ConditionalOnClass(ClientCredentialsResourceDetails::class, LoadBalancerClient::class)
@ConditionalOnProperty(prefix = "im.security.inner-oauth", name = ["enable"], havingValue = "true")
class InnerLoadBalanceAutoConfiguration(
    private val innerLoadBalanceProperties: InnerLoadBalanceProperties
) {
    @Bean
    fun loadBalancedResourceDetails(loadBalancerClient: LoadBalancerClient) = LoadBalancedResourceDetails(
        loadBalancerClient,
        innerLoadBalanceProperties.clientAuthorization.tokenServiceId
    )
        .apply {
            val clientAuthorization = innerLoadBalanceProperties.clientAuthorization
            accessTokenUri = clientAuthorization.accessTokenUri
            clientId = clientAuthorization.clientId
            clientSecret = clientAuthorization.clientSecret
        }
}