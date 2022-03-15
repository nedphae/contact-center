package com.qingzhu.staffadmin.staff.domain.dto

data class ReceptionistShuntDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long?,
    /** 客服分组 **/
    var groupId: Long,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 最大接待数量 */
    var maxServiceCount: Int = 8
)