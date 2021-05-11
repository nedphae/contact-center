package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface ReactiveShuntRepository : ReactiveSortingRepository<Shunt, Long> {
    fun findFirstByCode(code: String): Mono<Shunt>
}