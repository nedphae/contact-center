package com.qingzhu.imaccess.domain.query

/**
 * 已经分配的客服信息
 */
data class AssignmentInfo(
    /**
     * 如果存在就说明分配过客服了
     */
    val organizationId: Int,
    val conversationId: Long?,
    val staffId: Long?,
    val userId: Long,
    val customerConfig: CustomerConfig,
)
