package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ReactiveStaffGroupRepository : ReactiveSortingRepository<StaffGroup, Long> {
    fun findDistinctTopByGroupName(groupName: String): Mono<StaffGroup>
    fun findAllByOrganizationId(organizationId: Int): Flux<StaffGroup>
    fun deleteAllByIdIn(id: List<Long>): Mono<Void>
}