package com.qingzhu.bot.knowledgebase.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Deprecated("仅测试使用，后期需要删除")
@Service
class AuthProvider(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    // work on reactive code, not imperative code
    fun getStaffInfo(organizationId: Int, staffId: Int): Mono<String> {
        return webClient
                .get()
                .uri("/staff/info?organizationId=${organizationId}&staffId=${staffId}")
                .retrieve()
                .bodyToMono()
    }
}