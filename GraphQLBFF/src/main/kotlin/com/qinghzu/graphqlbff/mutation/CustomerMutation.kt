package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.model.Customer
import com.qinghzu.graphqlbff.webclient.CustomerService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class CustomerMutation(
    private val customerService: CustomerService,
) : Mutation {
    @GraphQLDescription("modifies passed in widget so it doesn't have null value")
    suspend fun updateCustomer(@GraphQLDescription("修改的客户基本信息") customer: Customer): Customer {
        return customerService.updateCustomer(customer.toMono()).awaitSingle()
    }
}