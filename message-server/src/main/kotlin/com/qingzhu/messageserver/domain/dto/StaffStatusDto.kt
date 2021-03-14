package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.messageserver.domain.authority.StaffAuthority
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.entity.StaffStatus
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffChangeStatusDto(
    /** 公司id */
    val organizationId: Int,
    /** 客服id */
    val staffId: Long,

    val userId: Long?
)

/**
 * 设置客服状态(初始状态)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: StaffAuthority,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 客服所处服务器名 */
    val clientAccessServer: Pair<String, String>,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /** 最大接待数量 */
    var maxServiceCount: Int
) {
    fun toStaffStatus(): StaffStatus = StaffStatusMapper.mapper.fromDtoWithMap(this)
}

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class StaffStatusMapper {
    protected abstract fun mapFromDto(staff: StaffStatusDto): StaffStatus

    fun fromDtoWithMap(staff: StaffStatusDto): StaffStatus {
        val status = mapFromDto(staff)
        status.clientAccessServerMap += staff.clientAccessServer
        return status
    }

    companion object {
        val mapper: StaffStatusMapper = Mappers.getMapper(StaffStatusMapper::class.java)
    }
}