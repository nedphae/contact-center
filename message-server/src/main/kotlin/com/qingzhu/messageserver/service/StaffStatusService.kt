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
import com.qingzhu.messageserver.domain.dto.StaffStatusDto
import com.qingzhu.messageserver.domain.dto.UpdateStaffStatus
import com.qingzhu.messageserver.domain.entity.StaffStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

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

    fun replaceStatus(oldStaffStatus: StaffStatus, staffStatus: StaffStatus): Boolean {
        val statusMap = getStatusMap(staffStatus.organizationId)
        statusMap.setTtl(staffStatus.staffId, 2, TimeUnit.HOURS)
        return statusMap.replace(staffStatus.staffId, oldStaffStatus, staffStatus)
    }

    fun replaceStatusWithException(oldStaffStatus: StaffStatus?, staffStatus: StaffStatus) {
        if (oldStaffStatus != null) {
            if (!replaceStatus(oldStaffStatus, staffStatus)) throw ConcurrentModificationException()
        } else {
            saveStatus(staffStatus)
        }
    }

    fun registerOrUpdateStaff(staffStatusDto: StaffStatusDto): Mono<StaffStatus> {
        return consistencyUpdate(staffStatusDto.organizationId, staffStatusDto.staffId)
        {
            map {
                val newStaffStatus = staffStatusDto.toStaffStatus(it)
                newStaffStatus
            }.switchIfEmpty {
                val newStaffStatus = staffStatusDto.toStaffStatus()
                newStaffStatus.toMono()
            }
        }
    }

    /**
     * 新增或更新状态，如果状态被更新过，重新获取并再次更新
     */
    fun consistencyUpdate(
        organizationId: Int,
        staffId: Long,
        run: Mono<StaffStatus>.() -> Mono<StaffStatus>
    ): Mono<StaffStatus> {
        val oldStaffStatus = AtomicReference<StaffStatus>()
        return findStaff(organizationId, staffId)
            .map {
                oldStaffStatus.set(it)
                // 返回 copy 对象
                it.deepCopy()
            }
            .transform(run)
            .doOnNext {
                replaceStatusWithException(oldStaffStatus.get(), it)
            }
            .retryWhen(Retry.indefinitely().filter { it is ConcurrentModificationException })
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
            staffStatus.setOffline(staffChangeStatusDto.clientId)
            statusMap.put(staffChangeStatusDto.staffId, staffStatus, 1, TimeUnit.HOURS)
        }
    }

    fun updateStaffStatus(updateStaffStatus: UpdateStaffStatus): Mono<StaffStatus> {
        return consistencyUpdate(updateStaffStatus.organizationId!!, updateStaffStatus.staffId!!)
        {
            doOnNext {
                it.onlineStatus = updateStaffStatus.onlineStatus
            }
        }
    }

    fun findStaff(organizationId: Int, staffId: Long): Mono<StaffStatus> {
        return Mono.create {
            val statusMap = getStatusMap(organizationId)
            it.success(statusMap[staffId])
        }
    }

    /**
     * 分配客服，有一定的不一致
     */
    fun assignment(staffChangeStatusDto: StaffChangeStatusDto): Mono<StaffStatus> {
        return consistencyUpdate(staffChangeStatusDto.organizationId, staffChangeStatusDto.staffId)
        {
            filter {
                !it.autoBusy && it.onlineStatus == OnlineStatus.ONLINE
            }.map {
                // 重新保存状态
                it.currentServiceCount++
                it.userIdList.add(staffChangeStatusDto.userId!!)
                it
            }
        }
    }

    fun removeCustomer(organizationId: Int, staffId: Long, userId: Long) {
        consistencyUpdate(organizationId, staffId)
        {
            map {
                // 重新保存状态
                it.currentServiceCount--
                it.userIdList.remove(userId)
                it
            }
        }.subscribe()
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
