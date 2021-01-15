package com.qingzhu.dispatcher.component

import com.qingzhu.common.component.BaseWebClient
import com.qingzhu.dispatcher.domain.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MessageService(webClientBuilder: WebClient.Builder, baseUrl: String) :
        BaseWebClient(webClientBuilder, baseUrl) {

    @Autowired
    constructor(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
            this(webClientBuilder, "http://staff-admin")

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

    fun assignmentCustomer(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<Unit> {
        return webClient
                .put()
                .uri("/status/staff/assignment")
                .body(staffChangeStatusDto)
                .retrieve()
                .bodyToMono()
    }

    fun findStaffIdOrShuntIdOfCustomer(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return webClient
                .get()
                .uri {
                    it.path("/status/customer/shunt-id")
                            .queryParam("organizationId", organizationId)
                            .queryParam("userId", userId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }

    fun findConversationByUserId(@RequestParam("organizationId") organizationId: Int,
                                 @RequestParam("userId") userId: Long): Mono<ConversationStatusDto> {
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

    fun findIdleBotStaff(@RequestParam("organizationId") organizationId: Int,
                         @RequestParam("shuntId") shuntId: Long): Flux<StaffDispatcherDto> {
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
class StaffAdminService(webClientBuilder: WebClient.Builder, baseUrl: String) :
        BaseWebClient(webClientBuilder, baseUrl) {

    @Autowired
    constructor(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
            this(webClientBuilder, "http://staff-admin")

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