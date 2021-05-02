package com.qingzhu.staffadmin.staff.domain.dto

import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

data class InnerUser(
        val organizationId: Int,
        val id: Long = 0,
        val username: String,
        var password: String?,
        var role: String
)

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class StaffInnerMapper {
    abstract fun mapToInner(staff: Staff): InnerUser

    /**
     * Test default fun
     */
    fun mapToInnerWithPassword(staff: Staff): InnerUser {
        val innerUser = mapToInner(staff)
        innerUser.password = "123456"
        return innerUser
    }

    companion object {
        val mapper: StaffInnerMapper = Mappers.getMapper(StaffInnerMapper::class.java)
    }
}