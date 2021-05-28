package com.qingzhu.messageserver.service

import com.qingzhu.messageserver.domain.dto.Customer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono

/**
 * 使用 webflux webclient 优化
 */
@Service
class DispatchingCenter(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://dispatching-center").build()

    fun findCustomer(organizationId: Int, userId: Long): Mono<Customer> {
        return webClient
            .get()
            .uri {
                it.path("/customer")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

}