package com.qingzhu.dispatcher.customer.service

import com.qingzhu.common.page.PageParam
import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.DispatcherApplicationTests
import com.qingzhu.dispatcher.customer.repo.dao.CustomerRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import reactor.test.StepVerifier

internal class CustomerServiceTest: DispatcherApplicationTests() {
    @Autowired
    private lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var customerService: CustomerService

    @Test
    fun testPage() {
        val page = customerRepository.findAllByOrganizationId(9491, PageRequest.of(0, 10))
        val test = page.doOnNext {
            println(it)
        }
        StepVerifier.create(test)
            .expectNextCount(1)
            .verifyComplete()

        runBlocking {
            val joinPage = customerService.findAllCustomer(9491, PageParam())
            println(joinPage.toJson())
            assertEquals(1, joinPage.content.size)
        }
    }
}