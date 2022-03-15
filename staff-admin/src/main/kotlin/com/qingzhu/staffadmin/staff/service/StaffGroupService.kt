package com.qingzhu.staffadmin.staff.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffGroupRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class StaffGroupService(
    private val staffGroupRepository: ReactiveStaffGroupRepository,
    private val reactorRedisCache: ReactorRedisCache,
) {
    fun findAllGroup(organizationId: Int): Flux<StaffGroup> {
        return reactorRedisCache.cache(
            "staff:group:$organizationId",
            staffGroupRepository.findAllByOrganizationId(organizationId)
        ) { JsonUtils.fromJson(it) }
    }

    fun saveGroup(staffGroup: StaffGroup): Mono<StaffGroup> {
        return staffGroupRepository.save(staffGroup)
            .flatMap { sg ->
                reactorRedisCache.removeKey("staff:group:${sg.organizationId}")
                    .map { sg }
            }
    }

    fun deleteAllByIds(organizationId: Int, ids: Flux<Long>): Mono<Long> {
        return ids.collectList().flatMap { staffGroupRepository.deleteAllByIdIn(it) }
            .then(reactorRedisCache.removeKey("staff:group:$organizationId"))
    }
}