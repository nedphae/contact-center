package com.qinghzu.graphqlbff.webclient

import com.qinghzu.graphqlbff.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * NOTE: 因为 这个是通过 kotlin coroutines 调用的，没有通过 spring reactive，所以无法获取 jwt token
 * 完成: innerWebClient 仅仅测试使用，后期修改为 bearerWebClient (使用 MyGraphQLContextFactory)
 */
@Component
class MessageService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://message-server") {

    fun findCustomerStatus(organizationId: Int, userId: Long): Mono<CustomerStatus> {
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

    fun searchConv(conversationQuery: ConversationQuery): Mono<SearchHitPage> {
        return webClient
            .post()
            .uri("/message/search")
            .bodyValue(conversationQuery)
            .retrieve()
            .bodyToMono()
    }

    fun findAllOnlineStaff(): Flux<StaffStatus> {
        return webClient
            .get()
            .uri("/status/staff/online")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllOnlineCustomer(): Flux<CustomerStatus> {
        return webClient
            .get()
            .uri("/status/customer/online")
            .retrieve()
            .bodyToFlux()
    }

    fun syncHistoryMessage(userId: Long, lastSeqId: Long?): Mono<MessagePage> {
        return webClient
            .get()
            .uri {
                it.path("/message/history/customer/sync")
                    .queryParam("userId", userId)
                    .queryParam("lastSeqId", lastSeqId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }
}

@Component
class CustomerService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://dispatching-center") {

    fun findAllCustomer(pageSize: Int, page: Int): Mono<CustomerPage> {
        return webClient
            .get()
            .uri {
                it.path("/customer/all")
                    .queryParam("pageSize", pageSize)
                    .queryParam("page", page)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

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

    fun findCustomerDetailData(organizationId: Int, userId: Long): Flux<DetailData> {
        return webClient
            .get()
            .uri {
                it.path("/customer/detail")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToFlux()
    }

    fun updateCustomer(customerDto: Mono<Customer>): Mono<Customer> {
        return webClient
            .post()
            .uri("/customer")
            .body(customerDto)
            .retrieve()
            .bodyToMono()
    }
}

@Service
class StaffAdminService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun findQuickReplyByStaff(): Mono<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/personal")
            .retrieve()
            .bodyToMono()
    }

    fun findQuickReplyByOrganizationId(): Mono<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/all")
            .retrieve()
            .bodyToMono()
    }

    fun findAllGroup(): Flux<StaffGroup> {
        return webClient
            .get()
            .uri("/staff/group")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllShunt(): Flux<Shunt> {
        return webClient
            .get()
            .uri("/staff/shunt")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllStaff(): Flux<Staff> {
        return webClient
            .get()
            .uri("/staff/all")
            .retrieve()
            .bodyToFlux()
    }

    fun saveQuickReply(quickReply: QuickReplyInput): Mono<QuickReply> {
        return webClient
            .post()
            .uri("/staff/quick-reply")
            .bodyValue(quickReply)
            .retrieve()
            .bodyToMono()
    }

    fun saveQuickReplyGroup(quickReplyGroup: QuickReplyGroupInput): Mono<QuickReplyGroup> {
        return webClient
            .post()
            .uri("/staff/quick-reply/group")
            .bodyValue(quickReplyGroup)
            .retrieve()
            .bodyToMono()
    }

    fun deleteQuickReply(id: Long): Mono<Void> {
        return webClient
            .delete()
            .uri("/staff/quick-reply/${id}")
            .retrieve()
            .bodyToMono()
    }

    fun deleteQuickReplyGroup(id: Long): Mono<Void> {
        return webClient
            .delete()
            .uri("/staff/quick-reply/group/${id}")
            .retrieve()
            .bodyToMono()
    }
}