package com.qingzhu.staffadmin.properties.repo.dao.jpa

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface PropertiesRepository : ReactiveCrudRepository<Properties, Long> {
    fun findDistinctTopByKey(key: String): Mono<Properties>
}