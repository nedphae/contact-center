package com.qingzhu.staffadmin.staff.repo.dao

import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface StaffConfigRepository : ReactiveCrudRepository<StaffConfig, Long> {
    fun findAllByOrganizationId(organizationId: Int): Flux<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<StaffConfig>
}