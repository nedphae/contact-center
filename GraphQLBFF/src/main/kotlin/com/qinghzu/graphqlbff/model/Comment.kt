package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.qingzhu.common.util.RawStringSerializer

class CommentPage : RestResponsePage<Comment>()

data class CommentQuery(
    var organizationId: Int? = null,
    var userId: Long? = null,
    val uid: String? = null,
    // 查询解决状态，nul：全部，0：未解决，1：已解决
    val solved: Int? = null,
    // 解决方式
    val solvedWay: Int? = null,
    /**
     * 时间
     */
    val timeRange: StringRangeQuery? = null,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
)

data class Comment(
    var organizationId: Int?,
    /** 留言时间 */
    val createdAt: Double,
    /** 留言时间 */
    /** 所属接待组 */
    val shuntId: Long,
    val userId: Long,
    val uid: String,
    val name: String,
    val mobile: String?,
    val email: String?,
    val message: String,
    val solved: Int,
    val solvedWay: Int? = null,
    val fromPage: String? = null,
    val fromIp: String? = null,
    @field:JsonDeserialize(using = RawStringSerializer::class)
    val geo: String? = null,
    // 负责客服
    val responsible: Long? = null,
) {
    val id: String
        get() = "${this.organizationId}:${this.shuntId}:${this.createdAt}"

}