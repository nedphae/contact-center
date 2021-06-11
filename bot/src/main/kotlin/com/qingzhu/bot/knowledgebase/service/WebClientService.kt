package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import com.qingzhu.bot.knowledgebase.domain.dto.StaffWithShuntDto
import com.qingzhu.common.component.BaseWebClient
import com.qingzhu.common.domain.shared.msg.value.Message
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux
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

    fun send(message: Message): Mono<ResponseEntity<Unit>> {
        return webClient
            .post()
            .uri("/message/sync")
            .bodyValue(message)
            .retrieve()
            .toEntity()
    }
}

@Service
class StaffAdminService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun findAllEnabledBotStaff(): Flux<StaffWithShuntDto> {
        return webClient
            .get()
            .uri("/staff/bots")
            .retrieve()
            .bodyToFlux()
    }
}
