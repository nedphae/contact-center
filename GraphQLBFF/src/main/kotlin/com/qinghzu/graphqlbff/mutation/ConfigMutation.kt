package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.ShuntUIConfig
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("系统配置信息修改")
@Component
class ConfigMutation(private val staffAdminService: StaffAdminService): Mutation {
    suspend fun saveChatUIConfig(@GraphQLIgnore context: MyGraphQLContext, uiConfig: ShuntUIConfig): ShuntUIConfig? {
        return staffAdminService.saveUIConfig(uiConfig).awaitWithAuthentication(context.oAuth)
    }
}