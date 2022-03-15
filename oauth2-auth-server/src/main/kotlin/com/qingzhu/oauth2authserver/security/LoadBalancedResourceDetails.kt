package com.qingzhu.oauth2authserver.security

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import java.net.URI
import java.net.URISyntaxException


/**
 * 服务内部调用 使用 grant_type = client_credentials 授权方式
 * 根据clientId & clientSecret配置自动签发 token
 */
@ConditionalOnMissingBean
class LoadBalancedResourceDetails(
    private val loadBalancerClient: LoadBalancerClient?,
    private val tokenServiceId: String?
) : ClientCredentialsResourceDetails() {
    companion object {
        private val log = LoggerFactory.getLogger(LoadBalancedResourceDetails::class.java)
    }

    override fun getAccessTokenUri(): String {
        return if (loadBalancerClient != null && !tokenServiceId.isNullOrBlank()) {
            try {
                loadBalancerClient.reconstructURI(
                    loadBalancerClient.choose(tokenServiceId),
                    URI(super.getAccessTokenUri())
                ).toString()
            } catch (ex: URISyntaxException) {
                log.error("Returning an invalid URI: {}", ex.message)
                super.getAccessTokenUri()
            }
        } else {
            super.getAccessTokenUri()
        }
    }
}