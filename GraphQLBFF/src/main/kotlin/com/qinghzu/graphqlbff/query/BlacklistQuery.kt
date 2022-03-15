package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Blacklist
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class BlacklistQuery(private val customerService: CustomerService) : Query {
    @GraphQLDescription("获取全部黑名单")
    suspend fun getAllBlacklist(
        @GraphQLIgnore context: MyGraphQLContext,
        audited: Boolean,
    ): List<Blacklist>? {
        return customerService.getAllBlacklist(audited).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("根据 UID 或者 IP 获取黑名单")
    suspend fun getBlacklistBy(
        @GraphQLIgnore context: MyGraphQLContext,
        @GraphQLDescription("黑名单策略 UID/IP")
        preventStrategy: String,
        @GraphQLDescription("黑名单对象 用户UID, 或者 用户IP 字符串")
        preventSource: String,
    ): Blacklist? {
        return customerService.getBlacklistBy(preventStrategy, preventSource).awaitWithAuthentication(context.oAuth)
    }
}