package com.qingzhu.staffadmin.staff.domain.dto

import com.qingzhu.staffadmin.staff.domain.entity.Staff

data class StaffDto(
        /** 公司id */
        val organizationId: Int,
        /** 客服id */
        val staffId: Long,
        val groupId: Long,
        val username: String,
        val realName: String,
        val nickName: String,
        val gender: Int,
        val personalizedSignature: String?
) {
    companion object {
        fun fromStaff(staff: Staff): StaffDto {
            return StaffDto(
                    organizationId = staff.organizationId,
                    staffId = staff.id!!,
                    groupId = staff.staffGroupId,
                    username = staff.username,
                    realName = staff.realName,
                    nickName = staff.nickName,
                    gender = staff.gender,
                    personalizedSignature = staff.personalizedSignature
            )
        }
    }
}