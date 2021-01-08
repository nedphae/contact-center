package com.qingzhu.bot.config

import com.qingzhu.bot.BotApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@LoadBalancerClient(name = "staff-admin")
class WebClientConfig {
    @LoadBalanced
    @Bean
    fun webClient(clientRegistrationRepository: ReactiveClientRegistrationRepository,
                  authorizedClientRepository: ServerOAuth2AuthorizedClientRepository,
                  loadBalancerExchangeFilterFunction: ReactorLoadBalancerExchangeFilterFunction): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository)
        oauth.setDefaultOAuth2AuthorizedClient(true)
        return WebClient.builder()
                .filter(loadBalancerExchangeFilterFunction)
                .filter(oauth)
                .build()
    }
}