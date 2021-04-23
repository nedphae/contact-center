package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.qingzhu.messageserver.domain.dto.CustomerBaseClientDto
import com.qingzhu.messageserver.domain.dto.CustomerBaseStatusDto
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
            it.clientAccessServerMap.putAll(customerStatus.clientAccessServerMap)
            statusMap[it.userId] = it
        }
        statusMap.setTtl(customerStatus.userId, 1, TimeUnit.HOURS)
    }

    fun setStatusOffline(customerBaseStatusDto: CustomerBaseStatusDto) {
        val statusMap = getStatusMap(customerBaseStatusDto.organizationId)
        val customerStatus = statusMap[customerBaseStatusDto.userId]
        if (customerStatus != null) {
            customerStatus.setOffline(customerBaseStatusDto.accessServerClient)
            statusMap.put(customerBaseStatusDto.userId, customerStatus, 15, TimeUnit.MINUTES)
        }
    }

    fun findStaffIdOrShuntId(organizationId: Int, userId: Long): Mono<CustomerStatus> {
        val statusMap = getStatusMap(organizationId)
        return Mono.justOrEmpty(statusMap[userId])
    }

    fun findByUserId(organizationId: Int, userId: Long): Mono<CustomerStatus> {
        val statusMap = getStatusMap(organizationId)
        return Mono.justOrEmpty(statusMap[userId])
    }

    fun findByUid(organizationId: Int, uid: String): Mono<CustomerStatus> {
        val statusMap = getStatusMap(organizationId)
        val equalPredicate = EqualPredicate("uid", uid)

        @Suppress("UNCHECKED_CAST")
        return Mono
                .justOrEmpty(statusMap.values(equalPredicate as Predicate<Long, CustomerStatus>)
                        .stream().findFirst())
    }

    fun updateByClientId(customerBaseClientDto: CustomerBaseClientDto): Mono<CustomerStatus> {
        return findByUserId(customerBaseClientDto.organizationId, customerBaseClientDto.userId)
                .map {
                    it.clientAccessServerMap += customerBaseClientDto.clientAccessServer
                    saveStatus(it)
                    it
                }
    }
}
