package com.qingzhu.staffadmin.staff.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntClassRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ShuntService(
    private val shuntRepository: ReactiveShuntRepository,
    private val shuntClassRepository: ReactiveShuntClassRepository,
    private val reactorRedisCache: ReactorRedisCache,
) {
    fun findAllShunt(organizationId: Int): Flux<Shunt> {
        return reactorRedisCache.cache(
            "staff:shunt:$organizationId",
            shuntRepository.findAllByOrganizationId(organizationId)
        ) { JsonUtils.fromJson(it) }
    }

    fun saveShunt(shunt: Shunt): Mono<Shunt> {
        if (shunt.code.isNullOrEmpty()) {
            shunt.code = UUID.randomUUID().toString()
        }
        return shuntRepository.save(shunt)
    }

    fun findAllShuntClass(organizationId: Int): Flux<ShuntClass> {
        return reactorRedisCache
            .with(shuntClassRepository.findAllByOrganizationId(organizationId))
            .key("staff:shunt:class:$organizationId")
            .deserialize { JsonUtils.fromJson(it) }
            .cacheFlux()
    }

    fun saveShuntClass(shuntClass: ShuntClass): Mono<ShuntClass> {
        return shuntClassRepository.save(shuntClass)
    }
}