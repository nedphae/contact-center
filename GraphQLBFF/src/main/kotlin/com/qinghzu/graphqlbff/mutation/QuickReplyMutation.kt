package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.*
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class QuickReplyMutation(private val staffAdminService: StaffAdminService) : Mutation {
    @GraphQLDescription("添加快捷回复")
    suspend fun addQuickReply(@GraphQLIgnore context: MyGraphQLContext, quickReplyInput: QuickReplyInput): QuickReply? {
        return staffAdminService.saveQuickReply(quickReplyInput).awaitWithAuthentication(context.oAuth)

    }
    @GraphQLDescription("添加快捷回复分组")
    suspend fun addQuickReplyGroup(@GraphQLIgnore context: MyGraphQLContext, quickReplyGroupInput: QuickReplyGroupInput): QuickReplyGroup? {
        return staffAdminService.saveQuickReplyGroup(quickReplyGroupInput).awaitWithAuthentication(context.oAuth)
    }

    /**
     * 根据[id]删除后 使用 subQuery 再次查询
     */
    @GraphQLDescription("根据[id]删除快捷回复后 使用 subQuery 再次查询")
    suspend fun deleteQuickReply(@GraphQLIgnore context: MyGraphQLContext, id: Long): Long {
        staffAdminService.deleteQuickReply(id).awaitWithAuthentication(context.oAuth)
        return id
    }

    @GraphQLDescription("根据[id]删除快捷回复分组后 使用 subQuery 再次查询")
    suspend fun deleteQuickReplyGroup(@GraphQLIgnore context: MyGraphQLContext, id: Long): Long {
        staffAdminService.deleteQuickReplyGroup(id).awaitWithAuthentication(context.oAuth)
        return id
    }
}
