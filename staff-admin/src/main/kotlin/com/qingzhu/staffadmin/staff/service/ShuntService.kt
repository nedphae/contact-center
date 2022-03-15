package com.qingzhu.staffadmin.staff.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.dto.ShuntDto
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntClassRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import com.qingzhu.staffadmin.staff.repository.ShuntUIConfigRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class ShuntService(
    private val shuntRepository: ReactiveShuntRepository,
    private val shuntClassRepository: ReactiveShuntClassRepository,
    private val shuntUIConfigRepository: ShuntUIConfigRepository,
    private val reactorRedisCache: ReactorRedisCache,
) {

    fun findFirstByCode(code: String): Mono<ShuntDto> {
        val shuntDto = shuntRepository.findFirstByCode(code)
            .flatMap { shunt ->
                shuntUIConfigRepository.findByOrganizationIdAndShuntId(shunt.organizationId!!, shunt.id!!)
                    .map {
                        DtoMapper.mapper.mapShuntToDto(shunt).also { sd ->
                            sd.config = it.config
                        }
                    }
                    .switchIfEmpty(DtoMapper.mapper.mapShuntToDto(shunt).toMono())
            }
        return reactorRedisCache
            .with(shuntDto)
            .key("staff:shunt:$code")
            .deserialize { JsonUtils.fromJson(it) }
            .cacheMono()
    }

    fun findAllShunt(organizationId: Int): Flux<Shunt> {
        return reactorRedisCache
            .with(shuntRepository.findAllByOrganizationId(organizationId))
            .key("staff:shunt:$organizationId")
            .deserialize { JsonUtils.fromJson(it) }
            .cacheFlux()
    }

    fun saveShunt(shunt: Shunt): Mono<Shunt> {
        if (shunt.code.isNullOrEmpty()) {
            shunt.code = UUID.randomUUID().toString()
        }
        return shuntRepository.save(shunt)
            .flatMap { saved ->
                reactorRedisCache.removeKey(
                    "staff:shunt:${shunt.organizationId}",
                    "staff:shunt:${saved.code}"
                ).map { saved }
            }
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
            .flatMap { saved ->
                reactorRedisCache.removeKey("staff:shunt:class:${shuntClass.organizationId}").map { saved }
            }
    }

    fun deleteAllByIds(organizationId: Int, ids: Flux<Long>): Mono<Long> {
        val idList = ids.collectList()
        val shuntList = shuntRepository.findAllById(ids).cache()
        val codeList = shuntList.mapNotNull { it.code }.collectList()
        return idList.flatMap { shuntUIConfigRepository.deleteAllByOrganizationIdAndShuntIdIn(organizationId, it) }
            .then(shuntRepository.deleteAll(shuntList))
            .then(
                codeList.map { code ->
                    code.map { "staff:shunt:$it" }
                }.flatMap {
                    reactorRedisCache.removeKey("staff:shunt:$organizationId", *it.toTypedArray())
                }
            )
    }
}