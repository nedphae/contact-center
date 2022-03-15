package com.qingzhu.staffadmin.staff.mapper

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.staffadmin.staff.domain.dto.*
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.QuickReply
import com.qingzhu.staffadmin.staff.domain.entity.QuickReplyGroup
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.query.StaffQuery
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class DtoMapper {
    companion object {
        val mapper: DtoMapper = Mappers.getMapper(DtoMapper::class.java)
    }

    abstract fun mapToInner(staff: Staff): InnerUser

    abstract fun mapShuntToDto(shunt: Shunt): ShuntDto

    /**
     * Test default fun
     */
    fun mapToInnerWithPassword(staff: Staff): InnerUser {
        val innerUser = mapToInner(staff)
        innerUser.password = "123456"
        return innerUser
    }

    abstract fun mapToStaffWithShuntDto(staff: Staff): StaffWithShuntDto

    abstract fun mapStaffQueryToStaff(staffQuery: StaffQuery): Staff

    /**
     * Test default fun
     */
    fun mapToInnerWithPassword(
        staff: Staff, shunt: List<Long>,
        priorityOfShunt: Map<Long, Int>,
    ): StaffWithShuntDto {
        val staffWithShuntDto = mapToStaffWithShuntDto(staff)
        staffWithShuntDto.shunt = shunt
        staffWithShuntDto.priorityOfShunt = priorityOfShunt
        return staffWithShuntDto
    }

    abstract fun mapToStaffQuickReplyGroupDto(quickReplyGroup: QuickReplyGroup): QuickReplyGroupDto

    abstract fun mapToStaffQuickReplyDto(quickReply: QuickReply): QuickReplyDto

    fun mapToStaffQuickReplyGroupDtoWithQuickReplyList(
        quickReplyGroup: QuickReplyGroup,
        quickReplyDtoList: List<QuickReplyDto>?
    ): QuickReplyGroupDto {
        val quickReplyGroupDto = mapToStaffQuickReplyGroupDto(quickReplyGroup)
        quickReplyGroupDto.quickReply = quickReplyDtoList
        return quickReplyGroupDto
    }
}
