package com.qinghzu.graphqlbff.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.fasterxml.jackson.annotation.JsonInclude

@GraphQLDescription("会话搜索")
data class ConversationQuery(
    @GraphQLDescription("关键字")
    val keyword: String?,
    /**
     * 时间
     */
    @GraphQLDescription("时间区间")
    val timeRange: StringRangeQuery?,
    /**
     * 责任客服
     */
    @GraphQLDescription("责任客服")
    val staffIdList: List<Long>?,
    /**
     * 咨询类型
     */
    @GraphQLDescription("咨询类型")
    val categoryList: List<String>?,
    /**
     * 客服组
     */
    @GraphQLDescription("客服组")
    val staffGroupIdList: List<Long>?,
    /**
     * 接待组
     */
    val shuntIdList: List<Long>?,
    /**
     * 总消息条数
     */
    @GraphQLDescription("总消息条数")
    val totalMessageCount: IntRangeQuery?,
    /**
     * 分页
     */
    @GraphQLDescription("分页参数")
    val page: PageParam = PageParam(),

    @GraphQLDescription("用户id")
    val userId: Long?,
)

class IntRangeQuery: RangeQuery<Int>()
class StringRangeQuery: RangeQuery<String>()

@JsonInclude(JsonInclude.Include.NON_NULL)
open class RangeQuery<T>(
    @GraphQLDescription("从")
    val from: T? = null,
    @GraphQLDescription("到")
    val to: T? = null,
    @GraphQLDescription("是否包括 从")
    val includeLower: Boolean? = true,
    @GraphQLDescription("是否包括 到")
    val includeUpper: Boolean? = true,
)

data class CustomerQuery(
    var organizationId: Int? = null,

    val keyword: String?,
    /**
     * 时间
     */
    val timeRange: StringRangeQuery?,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
)
