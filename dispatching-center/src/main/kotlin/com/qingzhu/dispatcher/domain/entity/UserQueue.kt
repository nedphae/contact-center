package com.qingzhu.dispatcher.domain.entity

import java.time.Instant

/**
 * 用户列队
 */
data class UserQueue(
    val organizationId: Int,
    val shuntId: Long,
    val userId: Long,
    val inQueueTime: Long = Instant.now().toEpochMilli()
)