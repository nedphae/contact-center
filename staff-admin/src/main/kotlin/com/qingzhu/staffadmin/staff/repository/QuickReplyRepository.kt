package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.QuickReply
import com.qingzhu.staffadmin.staff.domain.entity.QuickReplyGroup
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface QuickReplyRepository : ReactiveSortingRepository<QuickReply, Long> {
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<QuickReply>
    fun findAllByOrganizationIdAndPersonalIsFalse(organizationId: Int): Flux<QuickReply>
    fun findAllByGroupIdIsIn(groupId: List<Long>): Flux<QuickReply>
}

@Repository
interface QuickReplyGroupRepository : ReactiveSortingRepository<QuickReplyGroup, Long> {
    fun findAllByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Flux<QuickReplyGroup>
    fun findAllByOrganizationIdAndPersonalIsFalse(organizationId: Int): Flux<QuickReplyGroup>
}