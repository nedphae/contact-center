package com.qingzhu.staffadmin.properties.repository

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface PropertiesRepository : ReactiveSortingRepository<Properties, Long> {
    fun findDistinctTopByKey(key: String): Mono<Properties>
}