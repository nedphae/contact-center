package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.ShuntUIConfig
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("系统配置信息查询")
@Component
class ConfigQuery(private val staffAdminService: StaffAdminService): Query {
    suspend fun chatUIConfig(@GraphQLIgnore context: MyGraphQLContext, shuntId: Long): ShuntUIConfig? {
        return staffAdminService.getUIConfigByShunt(shuntId).awaitWithAuthentication(context.oAuth)
    }
}