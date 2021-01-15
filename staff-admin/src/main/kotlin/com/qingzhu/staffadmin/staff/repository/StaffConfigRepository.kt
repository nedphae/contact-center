package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface ReactiveStaffConfigRepository : ReactiveSortingRepository<StaffConfig, Long> {
    fun findAllByOrganizationId(organizationId: Int): Flux<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<StaffConfig>
}
interface StaffConfigRepository : JpaRepository<StaffConfig, Long> {
    fun findAllByOrganizationId(organizationId: Int): List<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): List<StaffConfig>
}