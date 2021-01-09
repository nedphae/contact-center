package com.qingzhu.imaccess.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.client.loadbalancer.LoadBalanced
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

    /**
     * 无授权的调用，仅添加负载功能
     */
    @Bean("loadBalancerWebClient")
    @LoadBalanced
    fun webClient(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun authorizedClientManager(@Qualifier("loadBalancerWebClient") webClientBuilder: WebClient.Builder,
                                clientRegistrationRepository: ReactiveClientRegistrationRepository,
                                authorizedClientService: ReactiveOAuth2AuthorizedClientService): ReactiveOAuth2AuthorizedClientManager {
        val loadBalancerClient = WebClientReactiveClientCredentialsTokenResponseClient()
        loadBalancerClient.setWebClient(webClientBuilder.build())
        val authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials { it.accessTokenResponseClient(loadBalancerClient) }
                .build()
        val authorizedClientManager = AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService)
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    /**
     * 内部客户端模式授权的服务间调用 client
     */
    @Bean("innerWebClient")
    fun innerWebClient(@Qualifier("loadBalancerWebClient") webClientBuilder: WebClient.Builder,
                       authorizedClientManager: ReactiveOAuth2AuthorizedClientManager): WebClient.Builder {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        return webClientBuilder.filter(oauth)
    }

    /**
     * 使用来自外部用户的权限
     */
    @Bean("bearerWebClient")
    fun bearerWebClient(@Qualifier("loadBalancerWebClient") webClientBuilder: WebClient.Builder): WebClient.Builder {
        val oauth = ServerBearerExchangeFilterFunction()
        return webClientBuilder.filter(oauth)
    }
}