package com.qingzhu.bot.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@LoadBalancerClient(name = "loadBalancer")
class WebClientConfig {

    @LoadBalanced
    @Bean("loadBalancerWebClient")
    fun webClient(loadBalancerExchangeFilterFunction: ReactorLoadBalancerExchangeFilterFunction): WebClient {
        return WebClient.builder()
                .filter(loadBalancerExchangeFilterFunction)
                .build()
    }

    @Bean
    fun authorizedClientManager(@Qualifier("loadBalancerWebClient") webClient: WebClient,
            clientRegistrationRepository: ReactiveClientRegistrationRepository,
            authorizedClientService: ReactiveOAuth2AuthorizedClientService): ReactiveOAuth2AuthorizedClientManager {
        val loadBalancerClient = WebClientReactiveClientCredentialsTokenResponseClient()
        loadBalancerClient.setWebClient(webClient)
        val authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials{ it.accessTokenResponseClient(loadBalancerClient) }
                .build()
        val authorizedClientManager = AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService)
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }

    @LoadBalanced
    @Bean
    fun webClient(authorizedClientManager: ReactiveOAuth2AuthorizedClientManager,
                  loadBalancerExchangeFilterFunction: ReactorLoadBalancerExchangeFilterFunction): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        return WebClient.builder()
                .filter(loadBalancerExchangeFilterFunction)
                .filter(oauth).build()
    }
}