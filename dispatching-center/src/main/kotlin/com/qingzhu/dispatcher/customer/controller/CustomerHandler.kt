package com.qingzhu.dispatcher.customer.controller

import com.qingzhu.common.page.PageParam
import com.qingzhu.common.security.awaitPrincipalTriple
import com.qingzhu.dispatcher.customer.domain.dto.CustomerDto
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

    suspend fun getCustomerById(sr: ServerRequest): ServerResponse {
        val (pOid, ) = sr.awaitPrincipalTriple()
        val oid = sr.queryParam("organizationId").map { it.toInt() }.orElse(pOid)
        val uid = sr.queryParam("userId").map(String::toLong).orElse(null)
        return ServerResponse.ok().bodyValueAndAwait(customerService.getCustomerById(oid, uid))
    }

    suspend fun getDetailDataByUserId(sr: ServerRequest): ServerResponse {
        val (pOid, ) = sr.awaitPrincipalTriple()
        val oid = sr.queryParam("organizationId").map { it.toInt() }.orElse(pOid)
        val uid = sr.queryParam("userId").map(String::toLong).orElse(null)
        return ServerResponse.ok().bodyAndAwait(customerService.getDetailDataByUserId(oid, uid))
    }

    suspend fun findAllCustomer(sr: ServerRequest): ServerResponse {
        val (oid, ) = sr.awaitPrincipalTriple()
        val page = sr.queryParam("page").map(String::toInt).orElse(0)
        val pageSize = sr.queryParam("pageSize").map(String::toInt).orElse(20)
        return ServerResponse.ok()
            .bodyValue(customerService.findAllCustomer(oid, PageParam(page, pageSize))).awaitSingle()
    }
}