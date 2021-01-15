package com.qingzhu.staffadmin.staff.repository.jpa

import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import org.springframework.data.jpa.repository.JpaRepository

interface StaffConfigRepository : JpaRepository<StaffConfig, Long> {
    fun findAllByOrganizationId(organizationId: Int): List<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): List<StaffConfig>
}