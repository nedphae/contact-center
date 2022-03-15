package com.qingzhu.staffadmin.staff.domain.query

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class StaffQuery(
    val id: Long?,
    var organizationId: Int?,
    // @field: NotEmpty(message = "用户名不能为空")
    // @field: Length(message = "密码长度必须大于3小于50", min = 6, max = 50)
    val username: String,
    // @field: Length(message = "密码长度必须大于6小于100", min = 6, max = 100)
    val password: String?,
    val role: StaffAuthority,
    // @field: NotNull(message = "用户分组不能为空")
    val staffGroupId: Long,
    // @field: NotNull(message = "用户实名不能为空")
    val realName: String,
    // @field: NotNull(message = "用户昵称不能为空")
    val nickName: String,
    // 头像URL
    val avatar: String?,
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
)