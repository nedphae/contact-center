package com.qingzhu.dispatcher.customer.domain.query

import com.qingzhu.common.domain.shared.RangeQuery
import com.qingzhu.common.page.PageParam
import com.qingzhu.dispatcher.customer.domain.constant.CommentSolved
import com.qingzhu.dispatcher.customer.domain.constant.CommentSolvedWay
import org.springframework.data.cassandra.core.query.Criteria
import org.springframework.data.cassandra.core.query.Query

data class CommentQuery(
    var organizationId: Int? = null,
    var userId: Long? = null,
    val uid: String? = null,
    // 查询解决状态，nul：全部，0：未解决，1：已解决
    val solved: CommentSolved? = null,
    // 解决方式
    val solvedWay: CommentSolvedWay? = null,
    /**
     * 时间
     */
    val timeRange: RangeQuery<String>? = null,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
) {
    fun buildSearchQuery(): Query {
        val criteriaDefinitions = mutableListOf(Criteria.where("organization_id").`is`(organizationId))
        if (!uid.isNullOrBlank()) {
            criteriaDefinitions.add(Criteria.where("uid").`is`(uid))
        }

        if (userId != null) {
            criteriaDefinitions.add(Criteria.where("user_id").`is`(userId))
        }

        if (solved != null) {
            criteriaDefinitions.add(Criteria.where("solved").`is`(solved))
        }
        if (solvedWay != null) {
            criteriaDefinitions.add(Criteria.where("solved_way").`is`(solvedWay))
        }
        if (timeRange != null) {
            if (timeRange.from != null) {
                criteriaDefinitions.add(Criteria.where("created_at").gte(timeRange.from!!))
            }
            if (timeRange.to != null) {
                criteriaDefinitions.add(Criteria.where("created_at").lt(timeRange.to!!))
            }
        }
        return Query.query(criteriaDefinitions)
            .pageRequest(page.toPageable())
    }
}