package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.ShuntUIConfig
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ShuntUIConfigRepository : ReactiveSortingRepository<ShuntUIConfig, Long> {
    fun findByOrganizationIdAndShuntId(organizationId: Int, shuntId: Long): Mono<ShuntUIConfig>

    fun findByOrganizationId(organizationId: Int): Flow<ShuntUIConfig>

    fun deleteAllByOrganizationIdAndShuntIdIn(organizationId: Int, shuntId: List<Long>): Mono<Void>
}