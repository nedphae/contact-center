package com.qingzhu.staffadmin.staff.domain.dto

import com.qingzhu.common.domain.shared.authority.StaffAuthority

data class StaffWithShuntDto(
    val id: Long,
    /** 公司id */
    val organizationId: Int,
    /** 用户名 */
    val username: String,
    /** 密码 */
    var password: String?,
    /** 角色 */
    var role: StaffAuthority,
    // 所属分组
    /** @ManyToOne */
    var staffGroupId: Long,
    /** 实名 */
    var realName: String,
    /** 昵称 */
    var nickName: String,
    /** 头像 **/
    var avatar: String?,
    /** 同时接待量（默认设置为8） */
    var simultaneousService: Int = 8,
    // 工单
    /** 每日上限 */
    var maxTicketPerDay: Int = 999,
    /** 总上限 */
    var maxTicketAllTime: Int = 999,
    /** 是否是机器人 0 机器人， 1人工 */
    var staffType: Int = 1,
    /** 性别 */
    var gender: Int = 0,
    /** 手机 */
    var mobilePhone: String? = null,
    /** 个性签名 */
    var personalizedSignature: String? = null,
    /** 是否启用 */
    var enabled: Boolean = true,
) {
    /** 所处接待组 */
    var shunt: List<Long> = emptyList()
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int> = emptyMap()
}