package com.qinghzu.graphqlbff.webclient

import com.qinghzu.graphqlbff.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
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

    fun searchConv(conversationQuery: ConversationQuery): Mono<String> {
        return webClient
            .post()
            .uri("/message/search")
            .bodyValue(conversationQuery)
            .retrieve()
            .bodyToMono()
    }
}

@Component
class CustomerService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://dispatching-center") {

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
class StaffAdminService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun findQuickReplyByStaff(): Flux<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/personal")
            .retrieve()
            .bodyToFlux()
    }

    fun findQuickReplyByOrganizationId(): Flux<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/all")
            .retrieve()
            .bodyToFlux()
    }
}