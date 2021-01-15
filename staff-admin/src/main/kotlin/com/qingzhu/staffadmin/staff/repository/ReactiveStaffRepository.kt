package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ReactiveStaffRepository : ReactiveSortingRepository<Staff, Long> {
    fun findFirstByOrganizationIdAndUsername(organizationId: Int, username: String): Mono<Staff>
}