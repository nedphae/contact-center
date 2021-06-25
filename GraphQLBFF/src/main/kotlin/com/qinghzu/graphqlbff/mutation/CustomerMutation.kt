package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Customer
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class CustomerMutation(
    private val customerService: CustomerService,
) : Mutation {
    @GraphQLDescription("修改的客户基本信息")
    suspend fun updateCustomer(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("修改的客户基本信息") customer: Customer): Customer? {
        return customerService.updateCustomer(customer.toMono()).awaitWithAuthentication(context.oAuth)
    }
}