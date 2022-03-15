package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReactiveStaffConfigRepository : ReactiveSortingRepository<StaffConfig, Long> {
    fun findAllByOrganizationIdAndStaffIdIn(organizationId: Int, staffIds: List<Long>): Flux<StaffConfig>
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<StaffConfig>
    fun findAllByOrganizationIdAndShuntId(organizationId: Int, shuntId: Long): Flux<StaffConfig>
    fun findAllByOrganizationId(organizationId: Int): Flux<StaffConfig>
    fun deleteAllByOrganizationIdAndShuntId(organizationId: Int, staffId: Long): Mono<Void>
}