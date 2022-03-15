package com.qingzhu.messageserver.domain.dto

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.messageserver.domain.constant.FromType
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import com.qingzhu.messageserver.mapper.CustomerStatusMapper
import java.time.Instant

data class CustomerBaseStatusDto(
    /** 公司id */
    val organizationId: Int,
    val userId: Long,
    val terminator: CreatorType,
    // 如果是机器人聊天（http） 就不用注册client了
    val clientAccessServer: String?,
)

data class CustomerBaseClientDto(
    /** 公司id */
    val organizationId: Int,
    val userId: Long,
    /** Which server i`m in
     * 如果需要配置登陆端互提，可将 A 更改为终端类型枚举
     */
    val clientAccessServer: Pair<String, String>
)

data class CustomerStatusDto(
    /** 公司id */
    val organizationId: Int,
    /** 客户系统id */
    val userId: Long,
    /** 客户提交id */
    val uid: String?,
    /** 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用 */
    val title: String?,
    /** 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用 */
    val referrer: String?,
    /** 指定客服id */
    var staffId: Long?,
    /** 指定客服组id */
    var groupId: Long?,
    /** 访客选择多入口分流模版id */
    val shuntId: Long,
    /** 机器人优先开关（访客分配） */
    val robotShuntSwitch: Int?,
    /** vip等级 1-10 */
    val vipLevel: Int?,
    /** 客户来源类型 */
    val fromType: FromType,
    /** 客户IP */
    val ip: String,
    /** 登录时间 */
    val loginTime: Instant = Instant.now(),
    //是否在线
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    // 客户待回复
    var needReply: Boolean = false,
    // 已经自动回复
    var autoReply: Boolean = false,
    // 客户待回复
    var lastReplyTime: Instant = Instant.now(),
) {
    fun toCustomerStatus(): CustomerStatus = CustomerStatusMapper.mapper.mapFromDto(this)
}