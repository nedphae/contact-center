package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ReactiveStaffRepository : ReactiveSortingRepository<Staff, Long> {
    fun findFirstByOrganizationIdAndUsernameAndStaffTypeAndEnabled(
        organizationId: Int,
        username: String,
        staffType: Int,
        enabled: Boolean
    ): Mono<Staff>

    fun findAllByStaffTypeAndEnabled(staffType: Int, enabled: Boolean): Flux<Staff>

    fun findAllByOrganizationId(organizationId: Int): Flux<Staff>

    fun deleteAllByIdIn(id: List<Long>): Mono<Void>
}