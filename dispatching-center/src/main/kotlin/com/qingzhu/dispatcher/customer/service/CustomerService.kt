package com.qingzhu.dispatcher.customer.service

import com.qingzhu.common.page.PageParam
import com.qingzhu.common.util.getNullPropertyNames
import com.qingzhu.dispatcher.customer.domain.dto.CustomerDto
import com.qingzhu.dispatcher.customer.domain.entity.Customer
import com.qingzhu.dispatcher.customer.domain.entity.DetailData
import com.qingzhu.dispatcher.customer.repo.dao.CustomerRepository
import com.qingzhu.dispatcher.customer.repo.dao.DetailDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.PageImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val detailDataRepository: DetailDataRepository,
) {
    @PreAuthorize("hasRole('ADMIN')")
    fun saveAndGetCustomer(customerDto: CustomerDto): Mono<CustomerDto> {
        val customer = customerDto.toCustomer()
        val dbCustomer = customerRepository.findFirstByOrganizationIdAndUid(customer.organizationId!!, customer.uid)
        return dbCustomer
            .flatMapMany { detailDataRepository.findAllByOrganizationIdAndUserId(it.organizationId!!, it.id!!) }
            .collectMap { it.key }
            .map {
                var result: List<DetailData> = emptyList()
                customerDto.data?.let { dataList ->
                    result = dataList.map { data ->
                        val old = it[data.key]
                        if (old != null) {
                            BeanUtils.copyProperties(data, old, *data.getNullPropertyNames())
                            old
                        } else data
                    }
                }
                customerDto.detailDataForUpdate?.let { dataList ->
                    result = dataList.mapNotNull { data ->
                        val old = it[data.key]
                        if (old != null) {
                            old.value = data.value
                            old
                        } else null
                    }
                }
                result
            }
            .flatMapIterable { it }
            .transform(detailDataRepository::saveAll)
            .then(dbCustomer)
            .map {
                BeanUtils.copyProperties(customer, it, *customer.getNullPropertyNames())
                it
            }.switchIfEmpty {
                Mono.just(customer)
            }.flatMap {
                // need id
                customerRepository.save(it).map { dto -> CustomerDto.fromCustomer(dto) }
            }
    }

    suspend fun getCustomerById(organizationId: Int? = null, userId: Long): Customer {
        return customerRepository.findById(userId).awaitSingle()
    }

    suspend fun getDetailDataByUserId(organizationId: Int, userId: Long): Flow<DetailData> {
        return detailDataRepository.findAllByOrganizationIdAndUserId(organizationId, userId).asFlow()
    }

    suspend fun findAllCustomer(organizationId: Int?, page: PageParam): PageImpl<CustomerDto> {
        val pageable = page.toPageable()
        return if (organizationId != null) {
            val count = customerRepository.countByOrganizationId(organizationId).awaitSingle()
            val customerPage = customerRepository
                .findAllByOrganizationId(organizationId, pageable).collectList().awaitSingle()
            val ids = customerPage.map { it.id ?: -1 }
            val detailListMap = detailDataRepository.findAllByOrganizationIdAndUserIdIn(organizationId, ids)
                .asFlow().toList().groupBy { it.userId }
            val list = customerPage.map {
                CustomerDto.fromCustomer(it, detailListMap[it.id])
            }
            PageImpl(list, pageable, count)
        } else PageImpl(emptyList(), pageable, 0)
    }
}