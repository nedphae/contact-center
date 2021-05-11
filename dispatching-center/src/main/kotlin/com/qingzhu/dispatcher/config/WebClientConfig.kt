package com.qingzhu.dispatcher.config

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun authorizedClientManager(
        lbFunction: ReactorLoadBalancerExchangeFilterFunction,
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
        authorizedClientService: ReactiveOAuth2AuthorizedClientService
    ): ReactiveOAuth2AuthorizedClientManager {
        val loadBalancerClient = WebClientReactiveClientCredentialsTokenResponseClient()
        loadBalancerClient.setWebClient(WebClient.builder().filter(lbFunction).build())
        val authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials { it.accessTokenResponseClient(loadBalancerClient) }
            .build()
        val authorizedClientManager = AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    /**
     * 内部客户端模式授权的服务间调用 client
     */
    @Bean("innerWebClient")
    fun innerWebClient(
        lbFunction: ReactorLoadBalancerExchangeFilterFunction,
        authorizedClientManager: ReactiveOAuth2AuthorizedClientManager
    ): WebClient.Builder {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth.setDefaultClientRegistrationId("authProvider")
        return WebClient.builder()
            .filter(lbFunction)
            .filter(oauth)
    }

    /**
     * 使用来自外部用户的权限
     */
    @Bean("bearerWebClient")
    fun bearerWebClient(lbFunction: ReactorLoadBalancerExchangeFilterFunction): WebClient.Builder {
        val oauth = ServerBearerExchangeFilterFunction()
        return WebClient.builder().filter(lbFunction).filter(oauth)
    }

}