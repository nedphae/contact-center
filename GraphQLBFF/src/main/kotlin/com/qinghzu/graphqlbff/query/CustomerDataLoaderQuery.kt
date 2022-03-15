package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.*
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class CustomerDataLoaderQuery(private val customerService: CustomerService) : Query {

    @GraphQLDescription("根据 机构id(oid), 和 用户id 获取用户信息")
    suspend fun getCustomer(@GraphQLIgnore context: MyGraphQLContext, userId: Long): Customer? {
        return customerService.findCustomer(userId).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("分页查询客户信息")
    suspend fun searchCustomer(
        @GraphQLIgnore context: MyGraphQLContext,
        customerQuery: CustomerQuery
    ): CustomerSearchHitPage? {
        return customerService.searchCustomer(customerQuery).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("获取客户会话信息")
    suspend fun getConversation(@GraphQLIgnore context: MyGraphQLContext, userId: Long): Conversation? {
        return customerService.findConversationByUserId(userId).awaitWithAuthentication(context.oAuth)
    }
}