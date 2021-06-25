package com.qingzhu.staffadmin.staff.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ShuntService(
    private val shuntRepository: ReactiveShuntRepository,
    private val reactorRedisCache: ReactorRedisCache,
) {
    fun findAllShunt(organizationId: Int): Flux<Shunt> {
        return reactorRedisCache.cache(
            "staff:shunt:$organizationId",
            shuntRepository.findAllByOrganizationId(organizationId)
        ) { JsonUtils.fromJson(it) }
    }

    fun saveShunt(shunt: Shunt): Mono<Shunt> {
        return shuntRepository.save(shunt)
    }
}