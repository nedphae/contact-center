package com.qingzhu.dispatcher.domain.dto

data class StaffDispatcherDto(
    /** 公司id */
    val organizationId: Int,
    /** 客服id */
    val staffId: Long,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Pair<Long, Int?>,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 当前接待量 */
    var currentServiceCount: Int,
    /** 客服类型，0 表示机器人，1 表示人工会话。 */
    val staffType: Int
)