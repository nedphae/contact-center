package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Properties
import com.qinghzu.graphqlbff.model.ShuntUIConfig
import com.qinghzu.graphqlbff.model.StaffConfig
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("系统配置信息修改")
@Component
class ConfigMutation(private val staffAdminService: StaffAdminService): Mutation {

    @GraphQLDescription("修改 ChatUI 配置")
    suspend fun saveChatUIConfig(@GraphQLIgnore context: MyGraphQLContext, uiConfig: ShuntUIConfig): ShuntUIConfig? {
        return staffAdminService.saveUIConfig(uiConfig).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("保存客服配置列表")
    suspend fun saveStaffConfig(@GraphQLIgnore context: MyGraphQLContext, staffConfigList: List<StaffConfig>): List<StaffConfig> {
        return staffAdminService.saveQuickReply(staffConfigList).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("更新系统配置列表")
    suspend fun updateProperties(@GraphQLIgnore context: MyGraphQLContext, properties: List<Properties>): List<Properties>? {
        return staffAdminService.updateProperties(properties).awaitWithAuthentication(context.oAuth)
    }
}