package com.qingzhu.imaccess.domain.dto

data class ReceptionistShuntDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 客服分组 **/
    var groupId: Long,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 最大接待数量 */
    var maxServiceCount: Int = 8
)

data class ShuntDto(
    /** 公司id */
    val organizationId: Int,
    // 接待组 id
    val id: Long,
    /** 接待组 名称 */
    val name: String,
    // 接待组所属分类
    /** @ManyToOne */
    val shuntClassId: Long,
    /** 接待组范围代码 */
    val code: String,
    // chat ui 配置
    val config: String,
)