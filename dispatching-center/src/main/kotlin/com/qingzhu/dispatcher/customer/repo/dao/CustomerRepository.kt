package com.qingzhu.dispatcher.customer.repo.dao

import com.qingzhu.dispatcher.customer.domain.entity.Customer
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/*
interface CustomerRepository : ReactiveSortingRepository<Customer, Long> {
    fun findFirstByOrganizationIdAndUid(organizationId: Int, uid: String): Mono<Customer>
    fun findAllByOrganizationId(organizationId: Int, pageRequest: Pageable): Flux<Customer>
    fun countByOrganizationId(organizationId: Int): Mono<Long>
    suspend fun deleteAllByIdIn(id: List<Long>)
}
*/
