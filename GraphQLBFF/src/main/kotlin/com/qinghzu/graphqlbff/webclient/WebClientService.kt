package com.qinghzu.graphqlbff.webclient

import com.qinghzu.graphqlbff.model.Customer
import com.qinghzu.graphqlbff.model.CustomerStatus
import com.qinghzu.graphqlbff.model.DetailData
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.*

/**
 * NOTE: 因为 这个是通过 kotlin coroutines 调用的，没有通过 spring reactive，所以无法获取 jwt token
 * TODO: innerWebClient 仅仅测试使用，后期修改为 bearerWebClient (使用 MyGraphQLContextFactory)
 */
@Component
class MessageService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://message-server") {

    suspend fun findCustomerStatus(organizationId: Int, userId: Long): CustomerStatus? {
        return webClient
            .get()
            .uri {
                it.path("/status/customer/find-by-id")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .awaitBodyOrNull()
    }
}

@Component
class CustomerService(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://dispatching-center") {

    suspend fun findCustomer(organizationId: Int, userId: Long): Customer? {
        return webClient
            .get()
            .uri {
                it.path("/customer")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .awaitBodyOrNull()
    }

    fun findCustomerDetailData(organizationId: Int, userId: Long): Flow<DetailData> {
        return webClient
            .get()
            .uri {
                it.path("/customer/detail")
                    .queryParam("organizationId", organizationId)
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToFlow()
    }
}