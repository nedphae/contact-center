package com.qingzhu.dispatcher.customer.repo.dao

import com.qingzhu.dispatcher.customer.domain.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findFirstByOrganizationIdAndUid(organizationId: Int, uid: String): Customer
}