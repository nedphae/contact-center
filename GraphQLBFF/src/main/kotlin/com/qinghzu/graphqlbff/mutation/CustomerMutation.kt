package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.model.Customer
import org.springframework.stereotype.Component

@Component
class CustomerMutation : Mutation {
    @GraphQLDescription("modifies passed in widget so it doesn't have null value")
    fun updateCustomer(@GraphQLDescription("修改的客户基本信息") customer: Customer): Customer {
        TODO()
    }
}