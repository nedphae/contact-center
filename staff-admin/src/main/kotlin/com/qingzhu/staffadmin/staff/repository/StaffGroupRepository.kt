package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ReactiveStaffGroupRepository : ReactiveSortingRepository<StaffGroup, Long> {
    fun findDistinctTopByGroupName(groupName: String): Mono<StaffGroup>
}

@Repository
interface StaffGroupRepository : JpaRepository<StaffGroup, Long>