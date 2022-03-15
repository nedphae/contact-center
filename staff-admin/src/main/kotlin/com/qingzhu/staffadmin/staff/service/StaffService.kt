package com.qingzhu.staffadmin.staff.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistShuntDto
import com.qingzhu.staffadmin.staff.domain.dto.StaffWithShuntDto
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffConfigRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import kotlin.streams.toList

@Service
class StaffService(
    private val staffRepository: ReactiveStaffRepository,
    private val staffConfigRepository: ReactiveStaffConfigRepository,
    private val reactorRedisCache: ReactorRedisCache,
) {

    fun findFirstByOrganizationIdAndUsername(organizationId: Int, username: String?): Mono<InnerUser> {
        return Mono.justOrEmpty(username)
            .flatMap {
                reactorRedisCache
                    .cache(
                        "staff:$organizationId:$username",
                        staffRepository.findFirstByOrganizationIdAndUsernameAndStaffTypeAndEnabled(
                            organizationId,
                            it,
                            1,
                            true
                        )
                    ) { str -> JsonUtils.fromJson(str) }
            }.map {
                InnerUser(it.organizationId!!, it.id!!, it.username, it.password, it.role.name)
            }.switchIfEmpty(Mono.error(UsernameNotFoundException("用户[$username]不存在")))
    }

    /**
     * 当前 spring 版本不支持直接获取 ROLE，需要自定义 AuthenticationConverter
     */
    // @PreAuthorize("hasRole('ADMIN')")
    fun findStaffInfo(staffId: Long): Mono<Staff> {
        return reactorRedisCache
            .cache(
                "staff:$staffId",
                staffRepository.findById(staffId)
            ) { JsonUtils.fromJson(it) }
    }

    fun findStaffConfigByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Mono<ReceptionistShuntDto> {
        val staff: Mono<Staff> = staffRepository.findById(staffId)
        val staffConfigFlux = reactorRedisCache
            .with(staffConfigRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId))
            .key("staff:config:$organizationId:$staffId")
            .deserialize { JsonUtils.fromJson(it) }
            .cacheFlux()

        return staffConfigFlux
            .collectMap({ it.shuntId }) { it.priority }
            .transform {
                Mono.zip(staff, it)
            }.map {
                ReceptionistShuntDto(
                    organizationId,
                    staffId,
                    it.t1.staffGroupId,
                    it.t2.keys.toList(),
                    it.t2,
                    it.t1.simultaneousService
                )
            }
    }

    fun findAllEnabledBotStaff(): Flux<StaffWithShuntDto> {
        val bots = staffRepository.findAllByStaffTypeAndEnabled(0, true).cache()
        val shuntMap = bots.collectMultimap { it.organizationId!! }
            .flatMapIterable { map ->
                map.keys.map { oid ->
                    val ids = map[oid]?.stream()?.map { it.id!! }?.toList() ?: emptyList()
                    reactorRedisCache
                        .with(staffConfigRepository.findAllByOrganizationIdAndStaffIdIn(oid!!, ids))
                        .key("staff:config:$oid:${ids.joinToString()}")
                        .deserialize { JsonUtils.fromJson(it) }
                        .cacheFlux()

                }
            }
            // 压扁
            .flatMapSequential { it }
            .collectMultimap { it.staffId!! }
            .cache()
        return bots.collectMap { it.id!! }
            .flatMapIterable { botMap ->
                botMap.map { entry ->
                    shuntMap.flatMap { map ->
                        // 根据 staff id 过滤
                        val flux = map[entry.key]?.toFlux() ?: Flux.empty()
                        flux.collectMap({ it.shuntId }) { it.priority }
                            .transform {
                                Mono.zip(entry.value.toMono(), it)
                            }
                            .map {
                                DtoMapper.mapper.mapToInnerWithPassword(it.t1, it.t2.keys.toList(), it.t2)
                            }
                    }
                }
            }
            // 压扁
            .flatMap { it }
    }

    fun findAllStaff(organizationId: Int): Flux<Staff> {
        return reactorRedisCache
            .cache(
                "staff:$organizationId",
                staffRepository.findAllByOrganizationId(organizationId)
            ) { JsonUtils.fromJson(it) }
    }

    fun findStaffConfigByShuntId(organizationId: Int, shuntId: Long): Flux<StaffConfig> {
        return staffConfigRepository.findAllByOrganizationIdAndShuntId(organizationId, shuntId)
    }

    fun saveStaffConfig(staffConfig: Flux<StaffConfig>, organizationId: Int): Flux<StaffConfig> {
        val cache = staffConfig.cache()
        return cache.next().flatMap {
            staffConfigRepository.deleteAllByOrganizationIdAndShuntId(organizationId, it.shuntId)
        }.thenMany(staffConfigRepository.saveAll(cache))
    }

    fun deleteStaffByIds(organizationId: Int, ids: Flux<Long>): Mono<Long> {
        return ids.collectList().flatMap { staffRepository.deleteAllByIdIn(it) }
            .then(reactorRedisCache.removeKey("staff:$organizationId"))
    }
}