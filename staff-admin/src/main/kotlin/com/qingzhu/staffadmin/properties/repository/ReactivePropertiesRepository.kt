package com.qingzhu.staffadmin.properties.repository

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReactivePropertiesRepository : ReactiveSortingRepository<Properties, Int> {
    fun findDistinctTopByOrganizationIdAndKeyAndPersonalIsFalse(organizationId: Int, key: String): Mono<Properties>
    fun findAllByOrganizationId(organizationId: Int): Flux<Properties>
    @Query("update Properties set value=:value where id=:id")
    fun updateValueById(id: Int, value: String): Mono<Void>
}