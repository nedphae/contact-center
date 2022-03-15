package com.qingzhu.dispatcher.component

import com.qingzhu.common.component.BaseWebClient
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.dispatcher.domain.dto.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MessageService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://message-server") {

    fun send(message: Mono<MessageDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .post()
            .uri("/message/send")
            .body(message)
            .retrieve()
            .toBodilessEntity()
    }

    fun findLatestStaffConvByUserId(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return webClient
            .get()
            .uri {
                it.path("/message/conversation/history/find-by-user-id")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun findIdleStaff(organizationId: Int, shuntId: Long): Flux<StaffDispatcherDto> {
        return webClient
            .get()
            .uri {
                it.path("/status/staff/idle")
                    .queryParam("organizationId", organizationId)
                    .queryParam("shuntId", shuntId)
                    .build()
            }
            .retrieve()
            .bodyToFlux()
    }

    fun assignmentCustomer(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri("/status/staff/assignment")
            .body(staffChangeStatusDto)
            .retrieve()
            .toBodilessEntity()
    }

    fun sendAssignmentSignal(conversationStatusDto: Mono<ConversationStatusDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .post()
            .uri("/message/send/assignment")
            .body(conversationStatusDto)
            .retrieve()
            .toBodilessEntity()
    }

    fun findStaffIdOrShuntIdOfCustomer(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return webClient
            .get()
            .uri {
                it.path("/status/customer/find-by-id")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun findConversationByUserId(
        @RequestParam("organizationId") organizationId: Int,
        @RequestParam("userId") userId: Long
    ): Mono<ConversationStatusDto> {
        return webClient
            .get()
            .uri {
                it.path("/status/conversation/find-by-user-id")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun findIdleBotStaff(
        @RequestParam("organizationId") organizationId: Int,
        @RequestParam("shuntId") shuntId: Long
    ): Flux<StaffDispatcherDto> {
        return webClient
            .get()
            .uri {
                it.path("/status/staff/bot/idle")
                    .queryParam("organizationId", organizationId)
                    .queryParam("shuntId", shuntId)
                    .build()
            }
            .retrieve()
            .bodyToFlux()
    }

    fun saveConversation(conversationStatusDto: Mono<ConversationStatusDto>): Mono<ConversationStatusDto> {
        return webClient
            .post()
            .uri("/status/conversation/save")
            .body(conversationStatusDto)
            .retrieve()
            .bodyToMono()
    }

    fun endConversation(conversationStatusDto: Mono<ConversationStatusDto>): Mono<Unit> {
        return webClient
            .put()
            .uri("/status/conversation/end")
            .body(conversationStatusDto)
            .retrieve()
            .bodyToMono()
    }
}

@Component
class StaffAdminService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://staff-admin") {

    fun getStaffInfo(organizationId: Int, staffId: Long): Mono<StaffDto> {
        return webClient
            .get()
            .uri {
                it.path("/staff/info")
                    .queryParam("organizationId", organizationId)
                    .queryParam("staffId", staffId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }
}