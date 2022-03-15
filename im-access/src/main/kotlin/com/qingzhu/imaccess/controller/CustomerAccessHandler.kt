package com.qingzhu.imaccess.controller

import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.service.CustomerAccessService
import com.qingzhu.imaccess.service.DispatchingCenter
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class CustomerAccessHandler(
    private val customerAccessService: CustomerAccessService,
    private val dispatchingCenter: DispatchingCenter,
) {
    /**
     *  用户注册
     */
    suspend fun register(sr: ServerRequest): ServerResponse {
        val customerConfig = sr.awaitBody<CustomerConfig>()
        customerConfig.ip = sr.remoteAddress().get().address.hostAddress
        val view = customerAccessService.register(customerConfig)
        return ok().bodyValueAndAwait(view)
    }

    suspend fun hasHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        return dispatchingCenter.findCustomer(userId)
            .flatMap {
                customerAccessService.hasHistoryMessage(it.organizationId, it.userId!!)
            }
            .flatMap { ok().bodyValue(it) }
            .awaitSingle()
    }

    suspend fun loadHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").filter(String::isNotBlank).map { it.toLong() }.orElse(null)
        val lastSeqId = sr.queryParam("lastSeqId").filter(String::isNotBlank).map { it.toLong() }.orElse(null)
        val pageSize = sr.queryParam("pageSize").filter(String::isNotBlank).map { it.toInt() }.orElse(null)
        return dispatchingCenter.findCustomer(userId)
            .flatMap {
                customerAccessService.loadHistoryMessage(it.organizationId, it.userId!!, lastSeqId, pageSize)
            }
            .flatMap { ok().bodyValue(it) }
            .awaitSingle()
    }

    suspend fun saveComment(sr: ServerRequest): ServerResponse {
        return sr.bodyToFlux<Any>()
            .transform { dispatchingCenter.saveComment(it) }
            .transform { ok().body(it) }
            .awaitSingle()
    }
}