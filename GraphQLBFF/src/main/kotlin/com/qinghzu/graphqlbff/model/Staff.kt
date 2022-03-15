package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.common.util.RawStringSerializer

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class OnlineStatus {
    OFFLINE,
    ONLINE,
    BUSY,
    AWAY
}

data class UpdateStaffStatus(
    var onlineStatus: OnlineStatus,
)

data class StaffStatus(
    /** 公司id */
    val organizationId: Int,
    /** 客服id
     *
     * 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: StaffAuthority,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 客服分组 **/
    var groupId: Long,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    val staffType: Int = 1,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    var pts: Long?,
    val loginTime: Double,
    /** 不同接待组的优先级 */
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var priorityOfShuntMap: String,
    var currentServiceCount: Int = 0,
    var autoBusy: Boolean,
    var userIdList: MutableList<Long>,
)

data class Staff(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int? = null,
    /** 用户名 */
    val username: String,
    /** 密码 机器人不需要密码 */
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
)

data class ShuntClass(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int,
    /** 分类名称 */
    val className: String,
    /** 上级分类 */
    val catalogue: Long
)

data class StaffConfig(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int? = null,
    /** 接待组 id */
    val shuntId: Long,
    /** 配置优先级 */
    var priority: Int,
    val staffId: Long,
)

data class Shunt(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int? = null,
    /** 接待组 名称 */
    val name: String,
    // 接待组所属分类
    /** @ManyToOne */
    val shuntClassId: Long,
    /** 接待组范围代码 */
    val code: String?
)

data class StaffGroup(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int? = null,
    /** 部门名称 */
    var groupName: String,
)

data class StaffUnion(
    val staffStatusList: List<StaffStatus>,
    val staffList: List<Staff>,
    val staffGroupList: List<StaffGroup>,
    val staffShuntList: List<Shunt>,
    val customerList: List<CustomerStatus>,
)