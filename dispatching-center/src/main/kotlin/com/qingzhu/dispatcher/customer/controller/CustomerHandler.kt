package com.qingzhu.dispatcher.customer.controller

import com.qingzhu.common.security.awaitPrincipalTriple
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.dispatcher.customer.domain.dto.CustomerDto
import com.qingzhu.dispatcher.customer.domain.query.CustomerQuery
import com.qingzhu.dispatcher.customer.service.CustomerService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*

@RestController
class CustomerHandler(
    private val customerService: CustomerService
) {
    suspend fun saveAndGetCustomer(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerDto>().map { customerDto ->
            customerService.saveAndGetCustomer(customerDto)
        }.flatMap { customerDto ->
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body<CustomerDto>(customerDto)
        }.awaitSingle()
    }

    suspend fun saveIfNotExist(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerDto>().map { customerDto ->
            customerService.saveIfNotExist(customerDto)
        }.flatMap { customerDto ->
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body<CustomerDto>(customerDto)
        }.awaitSingle()
    }

    suspend fun getCustomerById(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        return ServerResponse.ok().bodyValueAndAwait(customerService.getCustomerById(orgId, userId))
    }

    suspend fun searchCustomer(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitPrincipalTriple()
        return sr.bodyToMono<CustomerQuery>()
            .doOnNext { it.organizationId = orgId }
            .flatMap { ServerResponse.ok().body(customerService.searchCustomer(it)) }
            .awaitSingle()
    }

    suspend fun deleteByIds(sr: ServerRequest): ServerResponse {
        val ids = sr.bodyToFlow<Long>()
        customerService.deleteByIds(ids)
        return ServerResponse.ok().build().awaitSingle()
    }
}