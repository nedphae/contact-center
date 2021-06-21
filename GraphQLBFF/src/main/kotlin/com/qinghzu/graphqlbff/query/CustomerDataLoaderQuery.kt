package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Customer
import com.qinghzu.graphqlbff.model.CustomerPage
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class CustomerDataLoaderQuery(
    private val customerService: CustomerService,
) : Query {

    @GraphQLDescription("根据 机构id(oid), 和 用户id 获取用户信息")
    suspend fun getCustomer(@GraphQLIgnore context: MyGraphQLContext, oid: Int, userId: Long): Customer? {
        return customerService.findCustomer(oid, userId).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("分页获取客户信息")
    suspend fun findAllCustomer(@GraphQLIgnore context: MyGraphQLContext, first: Int, offset: Int): CustomerPage? {
        return customerService.findAllCustomer(first, offset).awaitWithAuthentication(context.oAuth)
    }
}