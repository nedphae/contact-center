package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.predicates.AndPredicate
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.hazelcast.query.impl.predicates.NotEqualPredicate
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.dto.StaffDispatcherDto
import com.qingzhu.messageserver.domain.entity.StaffStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.concurrent.TimeUnit

@Service
class StaffStatusService(
    @Qualifier("hazelcastInstance")
    private val hazelcastInstance: HazelcastInstance,
) {
    private fun getStatusMap(organizationId: Int) =
        hazelcastInstance.getMap<Long, StaffStatus>("$organizationId:staff")

    /**
     * 设置客服状态
     * 非原子化更新，可能数据丢失
     * [hazelcast 原子状态设置](https://docs.hazelcast.com/imdg/latest/computing/entry-processor.html)
     */
    fun saveStatus(staffStatus: StaffStatus) {
        val statusMap = getStatusMap(staffStatus.organizationId)
        if (statusMap.isEmpty) {
            statusMap.addIndex(IndexType.BITMAP, "shunt[any]")
            statusMap.addIndex(IndexType.HASH, "onlineStatus")
            statusMap.addIndex(IndexType.HASH, "autoBusy")
        }
        statusMap.put(staffStatus.staffId, staffStatus, 2, TimeUnit.HOURS)
    }

    /**
     * 获取 在线 ，状态就绪，空闲的客服
     */
    @Suppress("UNCHECKED_CAST")
    fun findIdleStaff(organizationId: Int, shuntId: Long, bot: Boolean = false): Collection<StaffStatus> {
        val statusMap = getStatusMap(organizationId)
        val predicateList: MutableList<Predicate<*, *>> = mutableListOf(
            // 在当前接待组
            EqualPredicate("shunt[any]", shuntId),
            // 在线
            EqualPredicate("onlineStatus", OnlineStatus.ONLINE),
            // 接待未满
            EqualPredicate("autoBusy", false),
        )
        if (bot) {
            predicateList.add(EqualPredicate("staffType", 0))
        } else {
            predicateList.add(EqualPredicate("staffType", 1))
        }
        val andPredicate = AndPredicate(*predicateList.toTypedArray())

        return statusMap.values(andPredicate as Predicate<Long, StaffStatus>)
    }

    fun findIdleStaffWithStaffDispatcherDto(organizationId: Int, shuntId: Long): Flow<StaffDispatcherDto> {
        return findIdleStaff(organizationId, shuntId)
            .asFlow()
            .map { StaffDispatcherDto.fromStaffStatusAndShuntId(shuntId, it) }
    }

    fun findBotStaffWithStaffDispatcherDto(organizationId: Int, shuntId: Long): Flow<StaffDispatcherDto> {
        return findIdleStaff(organizationId, shuntId, true)
            .asFlow()
            .map { StaffDispatcherDto.fromStaffStatusAndShuntId(shuntId, it) }
    }

    fun setStatusOffline(staffChangeStatusDto: StaffChangeStatusDto) {
        val statusMap = getStatusMap(staffChangeStatusDto.organizationId)
        val staffStatus = statusMap[staffChangeStatusDto.staffId]
        if (staffStatus != null) {
            staffStatus.setOffline()
            statusMap.put(staffChangeStatusDto.staffId, staffStatus, 1, TimeUnit.HOURS)
        }
    }

    fun findStaff(organizationId: Int, staffId: Long): Mono<StaffStatus> {
        val statusMap = getStatusMap(organizationId)
        return Mono.justOrEmpty(statusMap[staffId])
    }

    /**
     * 分配客服，有一定的不一致
     */
    fun assignment(staffChangeStatusDto: StaffChangeStatusDto): Mono<StaffStatus> {
        return findStaff(staffChangeStatusDto.organizationId, staffChangeStatusDto.staffId)
            .filter {
                !it.autoBusy && it.onlineStatus == OnlineStatus.ONLINE
            }
            .doOnNext {
                it.currentServiceCount++
                it.userIdList.add(staffChangeStatusDto.userId!!)
                // 重新保存状态
                saveStatus(it)
            }
    }

    fun removeCustomer(organizationId: Int, staffId: Long, userId: Long) {
        findStaff(organizationId, staffId)
            .doOnNext {
                it.currentServiceCount--
                it.userIdList.remove(userId)
            }
            .subscribe {
                // 重新保存状态
                saveStatus(it)
            }
    }

    /**
     * 获取所有在线客服列表
     * 管理员权限
     */
    @PreAuthorize("hasRole('ADMIN')")
    fun findAllOnlineStaff(organizationId: Int): Flux<StaffStatus> {
        val statusMap = getStatusMap(organizationId)
        val predicateList = mutableListOf(
            // 接待组不为空
            NotEqualPredicate("shunt[0]", null),
            // 在线
            NotEqualPredicate("onlineStatus", OnlineStatus.OFFLINE),
        )
        val andPredicate = AndPredicate(*predicateList.toTypedArray())
        @Suppress("UNCHECKED_CAST")
        return statusMap.values(andPredicate as Predicate<Long, StaffStatus>).toFlux()
    }
}
