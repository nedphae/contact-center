package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface ReactiveStaffConfigRepository : ReactiveSortingRepository<StaffConfig, Long> {
    fun findAllByOrganizationIdAndStaffIdIn(organizationId: Int, staffIds: List<Long>): Flux<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<StaffConfig>
}