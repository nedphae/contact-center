package com.qinghzu.graphqlbff.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.beans.factory.annotation.Autowired

@GraphQLDescription("快捷回复")
class QuickReplyAllDto {
    @GraphQLDescription("公司设置的快捷回复")
    suspend fun org(@GraphQLIgnore @Autowired staffAdminService: StaffAdminService, context: MyGraphQLContext) =
        staffAdminService.findQuickReplyByOrganizationId().awaitWithAuthentication(context.oAuth)
    @GraphQLDescription("个人设置的快捷回复")
    suspend fun personal(@GraphQLIgnore @Autowired staffAdminService: StaffAdminService, context: MyGraphQLContext) =
        staffAdminService.findQuickReplyByStaff().awaitWithAuthentication(context.oAuth)
}

data class QuickReplyDto(
    val withGroup: List<QuickReplyGroup>?,
    val noGroup: List<QuickReply>?,
)

data class QuickReplyGroup(
    val id: Long,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    val groupName: String,
    val personal: Boolean,
    val quickReply: List<QuickReply>?,
)

data class QuickReply(
    val id: Long,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    val groupId: Long?,
    val title: String,
    val content: String,
    val personal: Boolean,
)

data class QuickReplyInput(
    val id: Long?,
    var organizationId: Int?,
    var staffId: Long?,
    var groupId: Long?,
    var title: String,
    var content: String,
    var personal: Boolean,
)


data class QuickReplyGroupInput(
    val id: Long?,
    var organizationId: Int?,
    var staffId: Long?,
    val groupName: String,
    var personal: Boolean,
)