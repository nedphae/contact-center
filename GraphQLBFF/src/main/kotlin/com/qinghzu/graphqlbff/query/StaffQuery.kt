package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("客服信息查询")
@Component
class StaffQuery(private val staffAdminService: StaffAdminService) : Query {
    @GraphQLDescription("查询客服组")
    suspend fun staffGroup(@GraphQLIgnore context: MyGraphQLContext): String? {
        return staffAdminService.findAllGroup().awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("查询客服接待组")
    suspend fun staffShunt(@GraphQLIgnore context: MyGraphQLContext): String? {
        return staffAdminService.findAllShunt().awaitWithAuthentication(context.oAuth)
    }
}