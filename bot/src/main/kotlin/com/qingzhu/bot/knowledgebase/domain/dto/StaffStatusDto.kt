package com.qingzhu.bot.knowledgebase.domain.dto

import com.qingzhu.bot.knowledgebase.domain.constant.OnlineStatus

data class StaffChangeStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long
)

/**
 * 设置客服状态(初始状态)
 */
data class StaffStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: String,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /** 最大接待数量 */
    var maxServiceCount: Int = 10,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    val staffType: Int,
)