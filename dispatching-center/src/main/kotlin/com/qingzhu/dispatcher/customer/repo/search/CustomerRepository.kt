package com.qingzhu.dispatcher.customer.repo.search


import com.qingzhu.dispatcher.customer.domain.entity.Customer
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface CustomerRepository : ReactiveSortingRepository<Customer, Long> {
    fun findFirstByOrganizationIdAndUid(organizationId: Int, uid: String): Mono<Customer>
    fun deleteAllByIdIn(id: List<Long>): Mono<Void>
}