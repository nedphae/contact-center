package com.qingzhu.dispatcher.customer.repo.dao

import com.qingzhu.dispatcher.customer.domain.entity.DetailData
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

/*
interface DetailDataRepository : ReactiveSortingRepository<DetailData, Long> {
    fun findAllByOrganizationIdAndUserId(organizationId: Int, userId: Long): Flux<DetailData>
    fun findAllByOrganizationIdAndUserIdIn(organizationId: Int, userIds: List<Long?>): Flux<DetailData>
}
*/
