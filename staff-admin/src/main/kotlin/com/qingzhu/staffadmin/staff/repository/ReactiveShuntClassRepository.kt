package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface ReactiveShuntClassRepository : ReactiveSortingRepository<ShuntClass, Long> {
    fun findAllByOrganizationId(organizationId: Int): Flux<ShuntClass>
}