package com.qingzhu.staffadmin.staff.service

import com.qingzhu.staffadmin.staff.domain.dto.AllQuickReplyDto
import com.qingzhu.staffadmin.staff.domain.entity.QuickReply
import com.qingzhu.staffadmin.staff.domain.entity.QuickReplyGroup
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.QuickReplyGroupRepository
import com.qingzhu.staffadmin.staff.repository.QuickReplyRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.streams.toList

@Service
class QuickReplyService(
    private val quickReplyGroupRepository: QuickReplyGroupRepository,
    private val quickReplyRepository: QuickReplyRepository,
) {
    fun findQuickReplyByStaff(organizationId: Int, staffId: Long): Mono<AllQuickReplyDto> {
        val groups =
            quickReplyGroupRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId).cache()
        val staffQuickReplyFlux = quickReplyRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId)
        return getStaffQuickReplyWithGroupDto(groups, staffQuickReplyFlux)
    }

    fun findQuickReplyByOrganizationId(organizationId: Int): Mono<AllQuickReplyDto> {
        val groups =
            quickReplyGroupRepository.findAllByOrganizationIdAndPersonalIsFalse(organizationId).cache()
        val staffQuickReplyFlux = quickReplyRepository.findAllByOrganizationIdAndPersonalIsFalse(organizationId)
        return getStaffQuickReplyWithGroupDto(groups, staffQuickReplyFlux)
    }

    fun saveQuickReply(quickReply: QuickReply): Mono<QuickReply> {
        return quickReplyRepository.save(quickReply)
    }

    fun saveQuickReplyGroup(quickReplyGroup: QuickReplyGroup): Mono<QuickReplyGroup>{
        return quickReplyGroupRepository.save(quickReplyGroup)
    }

    fun deleteQuickReply(id: Long): Mono<Void> {
        return quickReplyRepository.deleteById(id)
    }

    fun deleteQuickReplyGroup(id: Long): Mono<Void> {
        return quickReplyGroupRepository.deleteById(id)
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
                        val staffQuickReplyDtoList = map[group.id]?.parallelStream()?.map {
                            DtoMapper.mapper.mapToStaffQuickReplyDto(it)
                        }?.toList()
                        DtoMapper.mapper.mapToStaffQuickReplyGroupDtoWithQuickReplyList(
                            group,
                            staffQuickReplyDtoList
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