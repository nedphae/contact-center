package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.view.ConversationView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.*
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 使用 webflux webclient 优化
 */
@Service
class DispatchingCenter(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://dispatching-center").build()

    fun findCustomer(userId: Long): Mono<CustomerDto> {
        return webClient
            .get()
            .uri {
                it.path("/customer")
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

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

    fun assignmentFromQueue(organizationId: Int, userId: Long): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri {
                it.path("/dispatcher/assignment/queue")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .toBodilessEntity()
    }

    fun assignmentFromQueueForStaff(staffStatusDto: StaffStatusDto): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri("/dispatcher/assignment/queue/for-staff")
            .bodyValue(staffStatusDto)
            .retrieve()
            .toBodilessEntity()
    }

    /**
     * 转发请求
     */
    fun saveComment(commentList: Flux<Any>): Flux<Any> {
        return webClient
            .post()
            .uri("/customer/comment")
            .body(commentList)
            .retrieve()
            .bodyToFlux()
    }
}

@Service
class MessageService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://message-server").build()

    /**
     * 返回 Mono<Unit> 就是返回空，会导致后续的 reactor 操作符不执行
     */
    fun send(message: Mono<MessageDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .post()
            .uri("/message/send")
            .body(message)
            .retrieve()
            .toBodilessEntity()
    }

    fun registerCustomer(customerDto: Mono<CustomerStatusDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .post()
            .uri("/status/register/customer")
            .body(customerDto)
            .retrieve()
            .toBodilessEntity()
    }

    /**
     * 注册客户客户端 Id
     */
    fun updateCustomerClient(customerDto: Mono<CustomerBaseClientDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri("/status/customer/update-client")
            .body(customerDto)
            .retrieve()
            .toBodilessEntity()
    }

    fun unregisterCustomer(customerDto: Mono<CustomerBaseStatusDto>): Mono<ResponseEntity<CustomerStatusDto>> {
        return webClient
            .put()
            .uri("/status/unregister/customer")
            .body(customerDto)
            .retrieve()
            .toEntity()
    }

    fun registerStaff(staffStatusDto: Mono<StaffStatusDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .post()
            .uri("/status/register/staff")
            .body(staffStatusDto)
            .retrieve()
            .toBodilessEntity()
    }

    fun unregisterStaff(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri("/status/unregister/staff")
            .body(staffChangeStatusDto)
            .retrieve()
            .toBodilessEntity()
    }

    fun findCustomerById(organizationId: Int, userId: Long): Mono<CustomerStatusDto> {
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

    fun hasHistoryMessage(organizationId: Int, userId: Long): Mono<Boolean> {
        return webClient
            .get()
            .uri {
                it.path("/message/has-history-msg")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun loadHistoryMessage(
        organizationId: Int,
        userId: Long,
        lastSeqId: Long?,
        pageSize: Int?
    ): Mono<RestResponsePage<Message>> {
        return webClient
            .get()
            .uri {
                it.path("/message/history")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .queryParam("pageSize", pageSize)
                    .queryParam("lastSeqId", lastSeqId)
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

    fun findDistinctTopPropByKey(organizationId: Int, key: String): Mono<Properties> {
        return webClient
            .get()
            .uri {
                it.path("/config/props/by-key")
                    .queryParam("organizationId", organizationId)
                    .queryParam("key", key)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }
}