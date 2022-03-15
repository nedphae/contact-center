package com.qingzhu.dispatcher.customer.service

import com.qingzhu.dispatcher.config.ReactorRedisCache
import com.qingzhu.dispatcher.customer.domain.dto.CustomerDto
import com.qingzhu.dispatcher.customer.domain.entity.Customer
import com.qingzhu.dispatcher.customer.domain.query.CustomerQuery
import com.qingzhu.dispatcher.customer.repo.search.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate,
    private val reactorRedisCache: ReactorRedisCache,
) {
    fun saveIfNotExist(customerDto: CustomerDto): Mono<CustomerDto> {
        val customer = customerDto.toCustomer()
        return reactorRedisCache
            .with(customerRepository.findFirstByOrganizationIdAndUid(customer.organizationId!!, customer.uid))
            .key("customer:${customer.organizationId}:${customer.uid}")
            .timeout(Duration.ofDays(30))
            .cacheMono()
            .switchIfEmpty(customerRepository.save(customer)).map { CustomerDto.fromCustomer(it) }
    }
    fun saveAndGetCustomer(customerDto: CustomerDto): Mono<CustomerDto> {
        val customer = customerDto.toCustomer()
        return customerRepository.save(customer).map { CustomerDto.fromCustomer(it) }
    /*
            .flatMapMany { detailDataRepository.findAllByOrganizationIdAndUserId(it.organizationId!!, it.id!!) }
            .collectMap { it.key }
            .map {
                var result: List<DetailData> = emptyList()
                customerDto.data?.let { dataList ->
                    result = dataList.map { data ->
                        // BeanUtils.copyProperties(data, old, *data.getNullPropertyNames())
                        it[data.key] ?: data
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
                // 更新非空字段
                BeanUtils.copyProperties(customer, it, *customer.getNullPropertyNames())
                it
            }.switchIfEmpty {
                Mono.just(customer)
            }.flatMap {
                // need id
                customerRepository.save(it).map { dto -> CustomerDto.fromCustomer(dto) }
            }
        */
    }

    suspend fun getCustomerById(organizationId: Int? = null, userId: Long): Customer {
        return customerRepository.findById(userId).awaitSingle()
    }

    /*
    suspend fun getDetailDataByUserId(organizationId: Int, userId: Long): Flow<DetailData> {
        return detailDataRepository.findAllByOrganizationIdAndUserId(organizationId, userId).asFlow()
    }
    */

    /**
     * 根据关键字查询客户信息
     */
    fun searchCustomer(conversationQuery: CustomerQuery): Mono<Page<SearchHit<Customer>>> {
        return reactiveElasticsearchTemplate
            .searchForPage(
                conversationQuery.buildSearchQuery().build(),
                Customer::class.java
            )
            // 缩小 SearchHit 导致的序列化太大
            .map { PageImpl(it.content, it.pageable, it.totalElements) }
    }

    suspend fun deleteByIds(ids: Flow<Long>) {
        customerRepository.deleteAllByIdIn(ids.toList())
    }
}