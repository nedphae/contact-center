package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.view.ConversationView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

/**
 * 使用 webflux webclient 优化
 */
@Service
class DispatchingCenter(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://dispatching-center").build()

    fun updateCustomer(customerDto: Mono<CustomerDto>): Mono<CustomerDto> {
        return webClient
                .post()
                .uri("/customer")
                .body(customerDto)
                .retrieve()
                .bodyToMono()
    }

    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationView> {
        return webClient
                .put()
                .uri {
                    it.path("/assignment/auto")
                            .queryParam("organizationId", organizationId)
                            .queryParam("userId", userId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }

    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        return webClient
                .put()
                .uri {
                    it.path("/assignment/staff")
                            .queryParam("organizationId", organizationId)
                            .queryParam("userId", userId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }
}

@Service
class MessageService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://message-server").build()

    fun send(message: Mono<MessageDto>): Mono<Unit> {
        return webClient
            .post()
            .uri("/message/send")
            .body(message)
            .retrieve()
            .bodyToMono()
    }

    fun registerCustomer(customerDto: Mono<CustomerStatusDto>): Mono<Unit> {
        return webClient
                .post()
                .uri("/register/customer")
                .body(customerDto)
                .retrieve()
                .bodyToMono()
    }

    fun unregisterCustomer(customerDto: Mono<CustomerBaseStatusDto>): Mono<Unit> {
        return webClient
                .put()
                .uri("/unregister/customer")
                .body(customerDto)
                .retrieve()
                .bodyToMono()
    }

    fun registerStaff(staffStatusDto: Mono<StaffStatusDto>): Mono<Unit> {
        return webClient
                .post()
                .uri("/register/staff")
                .body(staffStatusDto)
                .retrieve()
                .bodyToMono()
    }

    fun unregisterStaff(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<Unit> {
        return webClient
                .put()
                .uri("/unregister/staff")
                .body(staffChangeStatusDto)
                .retrieve()
                .bodyToMono()
    }

    fun findCustomerByUid(organizationId: Int, uid: String): Mono<CustomerBaseStatusDto> {
        return webClient
                .get()
                .uri {
                    it.path("/status/customer/find-by-uid")
                            .queryParam("organizationId", organizationId)
                            .queryParam("uid", uid)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }
}

@Service
class StaffAdminService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun getReceptionistGroup(organizationId: Int, staffId: Long): Mono<ReceptionistShuntDto> {
        return webClient
                .get()
                .uri {
                    it.path("/staff/receptionist")
                            .queryParam("organizationId", organizationId)
                            .queryParam("staffId", staffId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }
}