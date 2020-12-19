package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.qingzhu.messageserver.domain.dto.CustomerChangeStatusDto
import com.qingzhu.messageserver.domain.dto.CustomerDispatcherDto
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class CustomerStatusService(
        @Qualifier("hazelcastInstance")
        private val hazelcastInstance: HazelcastInstance
) {
    private fun getStatusMap(organizationId: Int) =
            hazelcastInstance.getMap<Long, CustomerStatus>("$organizationId:customer")

    /**
     * 设置客服状态
     */
    fun saveStatus(customerStatus: CustomerStatus) {
        val statusMap = getStatusMap(customerStatus.organizationId)
        if (statusMap.isEmpty) {
            statusMap.addIndex(IndexType.HASH, "uid")
        }
        // 这样写为了后面一些临时状态保存到 HazelcastInstance 不会丢失
        val oldCustomerStatus = statusMap.putIfAbsent(customerStatus.userId, customerStatus)
        oldCustomerStatus?.setOnline()?.also {
            it.redisHashKey = customerStatus.redisHashKey
            statusMap[it.userId] = it
        }
        statusMap.setTtl(customerStatus.userId, 2, TimeUnit.HOURS)
    }

    /**
     * TODO 添加 EntryListener 到 IMap， 删除 entry 同时会删除该 userId 关联的会话和 redis zSet 消息
     */
    fun setStatusOffline(customerChangeStatusDto: CustomerChangeStatusDto) {
        val statusMap = getStatusMap(customerChangeStatusDto.organizationId)
        val customerStatus = statusMap[customerChangeStatusDto.userId]
        if (customerStatus != null) {
            customerStatus.setOffline()
            statusMap.put(customerChangeStatusDto.userId, customerStatus, 1, TimeUnit.HOURS)
        }
    }

    fun findStaffIdOrShuntId(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        val statusMap = getStatusMap(organizationId)
        return Mono.justOrEmpty(statusMap[userId])
                .map { CustomerDispatcherDto.fromCustomerStatus(it!!) }
    }

    fun findCustomer(organizationId: Int, userId: Long): Mono<CustomerStatus> {
        val statusMap = getStatusMap(organizationId)
        return Mono.justOrEmpty(statusMap[userId])
    }

    fun assignmentStaff(staffChangeStatusDto: StaffChangeStatusDto): Mono<CustomerStatus> {
        return findCustomer(staffChangeStatusDto.organizationId, staffChangeStatusDto.userId!!)
                .doOnSuccess {
                    it.staffId = staffChangeStatusDto.staffId
                    it.isStaffService = true
                    // 重新保存状态
                    saveStatus(it)
                }
    }

    fun findByUid(organizationId: Int, uid: String): Mono<CustomerStatus> {
        val statusMap = getStatusMap(organizationId)
        val equalPredicate = EqualPredicate("uid", uid)

        @Suppress("UNCHECKED_CAST")
        return Mono
                .justOrEmpty(statusMap.values(equalPredicate as Predicate<Long, CustomerStatus>)
                        .stream().findFirst())
    }
}
