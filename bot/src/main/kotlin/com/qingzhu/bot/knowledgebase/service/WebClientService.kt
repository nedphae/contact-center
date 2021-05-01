package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.dto.StaffWithShuntDto
import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import com.qingzhu.common.component.BaseWebClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono

@Component
class MessageService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
        BaseWebClient(webClientBuilder, "http://message-server") {

    fun registerStaff(staffStatusDto: Mono<StaffStatusDto>): Mono<ResponseEntity<Unit>> {
        return webClient
            .post()
            .uri("/status/register/staff")
            .body(staffStatusDto)
            .retrieve()
            .toEntity()
    }

    fun getBotLock(): Mono<Boolean> {
        return webClient
                .post()
                .uri("/lock/bot/try")
                .retrieve()
                .bodyToMono()
    }

    fun releaseBotLock(): Mono<Unit> {
        return webClient
            .post()
            .uri("/lock/bot/release")
            .retrieve()
            .bodyToMono()
    }
}

@Service
class StaffAdminService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun findAllEnabledBotStaff(): Mono<StaffWithShuntDto> {
        return webClient
            .get()
            .uri("/staff/bots")
            .retrieve()
            .bodyToMono()
    }
}
