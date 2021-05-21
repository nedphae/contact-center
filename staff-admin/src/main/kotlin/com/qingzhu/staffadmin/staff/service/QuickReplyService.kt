package com.qingzhu.staffadmin.staff.service

import com.qingzhu.staffadmin.staff.domain.dto.AllQuickReplyDto
import com.qingzhu.staffadmin.staff.domain.entity.QuickReply
import com.qingzhu.staffadmin.staff.domain.entity.QuickReplyGroup
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.QuickRecoveryGroupRepository
import com.qingzhu.staffadmin.staff.repository.QuickRecoveryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.streams.toList

@Service
class QuickReplyService(
    private val quickRecoveryGroupRepository: QuickRecoveryGroupRepository,
    private val quickRecoveryRepository: QuickRecoveryRepository,
) {
    fun findQuickReplyByStaff(organizationId: Int, staffId: Long): Mono<AllQuickReplyDto> {
        val groups =
            quickRecoveryGroupRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId).cache()
        val staffQuickRecoveryFlux = quickRecoveryRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId)
        return getStaffQuickReplyWithGroupDto(groups, staffQuickRecoveryFlux)
    }

    fun findQuickReplyByOrganizationId(organizationId: Int): Mono<AllQuickReplyDto> {
        val groups =
            quickRecoveryGroupRepository.findAllByOrganizationIdAndPersonalIsFalse(organizationId).cache()
        val staffQuickRecoveryFlux = quickRecoveryRepository.findAllByOrganizationIdAndPersonalIsFalse(organizationId)
        return getStaffQuickReplyWithGroupDto(groups, staffQuickRecoveryFlux)
    }

    /**
     * 生成分组
     */
    private fun getStaffQuickReplyWithGroupDto(
        groups: Flux<QuickReplyGroup>,
        quickReplyFlux: Flux<QuickReply>
    ): Mono<AllQuickReplyDto> {
        return quickReplyFlux
            .collectMultimap { it.groupId ?: -1 }
            .flatMap { map ->
                val noGroup = map[-1]?.parallelStream()?.map {
                    DtoMapper.mapper.mapToStaffQuickReplyDto(it)
                }?.toList()
                groups
                    .map { group ->
                        val staffQuickRecoveryDtoList = map[group.id]?.parallelStream()?.map {
                            DtoMapper.mapper.mapToStaffQuickReplyDto(it)
                        }?.toList()
                        DtoMapper.mapper.mapToStaffQuickReplyGroupDtoWithQuickReplyList(
                            group,
                            staffQuickRecoveryDtoList
                        )
                    }
                    .collectList()
                    .map {
                        AllQuickReplyDto(
                            withGroup = it,
                            noGroup,
                        )
                    }
            }
    }
}