package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.view.ConversationView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity
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
                    it.path("/dispatcher/assignment/auto")
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
                    it.path("/dispatcher/assignment/staff")
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

    /**
     * 返回 Mono<Unit> 就是返回空，会导致后续的 reactor 操作符不执行
     */
    fun send(message: Mono<MessageDto>): Mono<ResponseEntity<Boolean>> {
        return webClient
            .post()
            .uri("/message/send")
            .body(message)
            .retrieve()
            .toEntity()
    }

    fun registerCustomer(customerDto: Mono<CustomerStatusDto>): Mono<ResponseEntity<Unit>> {
        return webClient
                .post()
                .uri("/status/register/customer")
                .body(customerDto)
                .retrieve()
                .toEntity()
    }

    /**
     * 注册客户客户端 Id
     */
    fun updateCustomerClient(customerDto: Mono<CustomerBaseClientDto>): Mono<ResponseEntity<Unit>> {
        return webClient
                .put()
                .uri("/status/customer/update-client")
                .body(customerDto)
                .retrieve()
                .toEntity()
    }

    fun unregisterCustomer(customerDto: Mono<CustomerBaseStatusDto>): Mono<ResponseEntity<Unit>> {
        return webClient
                .put()
                .uri("/status/unregister/customer")
                .body(customerDto)
                .retrieve()
                .toEntity()
    }

    fun registerStaff(staffStatusDto: Mono<StaffStatusDto>): Mono<ResponseEntity<Unit>> {
        return webClient
                .post()
                .uri("/status/register/staff")
                .body(staffStatusDto)
                .retrieve()
                .toEntity()
    }

    fun unregisterStaff(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<ResponseEntity<Unit>> {
        return webClient
                .put()
                .uri("/status/unregister/staff")
                .body(staffChangeStatusDto)
                .retrieve()
                .toEntity()
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

    fun getShuntByCode(code: String): Mono<ShuntDto> {
        return webClient
            .get()
            .uri("/staff/shunt/{code}", code)
            .retrieve()
            .bodyToMono()
    }
}