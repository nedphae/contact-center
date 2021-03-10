package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.constant.FromType
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import java.util.*

data class CustomerBaseStatusDto(
    /** 公司id */
    val organizationId: Int,
    val userId: Long,
    val accessServer: String
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
    /** 客服所处服务器 hash 值 */
    var accessServer: String? = null,
    /** vip等级 1-10 */
    val vipLevel: Int?,
    /** 客户来源类型 */
    val fromType: FromType,
    /** 客户IP */
    val ip: String
) {
    /** 登录时间 */
    val loginTime: Date = Date()

    //是否在线
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE

    fun setOffline() = apply { this.onlineStatus = OnlineStatus.OFFLINE; this.accessServer = null }

    fun setOnline() = apply { this.onlineStatus = OnlineStatus.ONLINE }
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CustomerStatusMapper {
    fun mapFromDto(staff: CustomerStatusDto): CustomerStatus

    companion object {
        val mapper: CustomerStatusMapper = Mappers.getMapper(CustomerStatusMapper::class.java)
    }
}