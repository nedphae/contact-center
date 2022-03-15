package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Blacklist
import com.qinghzu.graphqlbff.model.BlacklistInput
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class BlacklistMutation(private val customerService: CustomerService): Mutation {
    @GraphQLDescription("添加/修改 黑名单信息")
    suspend fun saveBlacklist(
        @GraphQLIgnore context: MyGraphQLContext,
        @GraphQLDescription("添加/修改 的黑名单信息列表") blacklist: List<BlacklistInput>
    ): List<Blacklist>? {
        return customerService.saveBlacklist(blacklist).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("删除黑名单信息")
    suspend fun deleteBlacklist(
        @GraphQLIgnore context: MyGraphQLContext,
        @GraphQLDescription("删除的 黑名单信息列表") blacklist: List<BlacklistInput>
    ): Long? {
        return customerService.deleteBlacklist(blacklist).awaitWithAuthentication(context.oAuth)
    }
}