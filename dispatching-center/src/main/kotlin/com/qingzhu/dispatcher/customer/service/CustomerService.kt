package com.qingzhu.dispatcher.customer.service

import com.qingzhu.common.util.getNullPropertyNames
import com.qingzhu.dispatcher.customer.domain.dto.CustomerDto
import com.qingzhu.dispatcher.customer.domain.entity.Customer
import com.qingzhu.dispatcher.customer.domain.entity.DetailData
import com.qingzhu.dispatcher.customer.repo.dao.CustomerRepository
import com.qingzhu.dispatcher.customer.repo.dao.DetailDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.BeanUtils
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
        val dbCustomer = customerRepository.findFirstByOrganizationIdAndUid(customer.organizationId, customer.uid)
        return dbCustomer.map {
            BeanUtils.copyProperties(customer, it, *customer.getNullPropertyNames())
            it
        }.switchIfEmpty {
            Mono.just(customer)
        }.flatMap {
            // need id
            customerRepository.save(it).map { dto -> CustomerDto.fromCustomer(dto) }
        }
    }

    suspend fun getCustomerById(organizationId: Int, userId: Long): Customer {
        return customerRepository.findById(userId).awaitSingle()
    }

    suspend fun getDetailDataByUserId(organizationId: Int, userId: Long): Flow<DetailData> {
        return detailDataRepository.findAllByOrganizationIdAndUserId(organizationId, userId).asFlow()
    }
}