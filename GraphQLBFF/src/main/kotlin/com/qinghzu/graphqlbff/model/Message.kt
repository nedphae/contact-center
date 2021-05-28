package com.qinghzu.graphqlbff.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.qingzhu.common.page.PageParam

@GraphQLDescription("会话搜索")
data class ConversationQuery(
    @GraphQLDescription("关键字")
    val keyword: String?,
    /**
     * 时间
     */
    @GraphQLDescription("时间区间")
    val timeRange: RangeQuery<String>?,
    /**
     * 责任客服
     */
    @GraphQLDescription("责任客服")
    @field: JsonSerialize(using = ToStringSerializer::class)
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
    @field: JsonSerialize(using = ToStringSerializer::class)
    val staffGroupId: List<Long>?,
    /**
     * 总消息条数
     */
    @GraphQLDescription("总消息条数")
    val totalMessageCount: RangeQuery<Int>?,
    /**
     * 分页
     */
    @GraphQLDescription("分页参数")
    val page: PageParam = PageParam(),
)

data class RangeQuery<T>(
    @GraphQLDescription("从")
    val from: T?,
    @GraphQLDescription("到")
    val to: T?,
    @GraphQLDescription("是否包括 从")
    val includeLower: Boolean = true,
    @GraphQLDescription("是否包括 到")
    val includeUpper: Boolean = true,
)