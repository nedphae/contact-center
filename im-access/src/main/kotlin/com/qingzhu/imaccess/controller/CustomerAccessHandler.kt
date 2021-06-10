package com.qingzhu.imaccess.controller

import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.service.BotAccessService
import com.qingzhu.imaccess.service.DispatchingCenter
import com.qingzhu.imaccess.service.MessageService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@RestController
class CustomerAccessHandler(
    private val botAccessService: BotAccessService,
    private val dispatchingCenter: DispatchingCenter,
    private val messageService: MessageService,
) {
    /**
     *  用户注册
     */
    suspend fun register(sr: ServerRequest): ServerResponse {
        val customerConfig = sr.awaitBody<CustomerConfig>()
        customerConfig.ip = sr.remoteAddress().get().address.hostAddress
        val view = botAccessService.register(customerConfig)
        return ok().bodyValueAndAwait(view)
    }

    suspend fun hasHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        return dispatchingCenter.findCustomer(userId)
            .flatMap {
                messageService.hasHistoryMessage(it.organizationId, it.userId!!)
            }
            .flatMap { ok().bodyValue(it) }
            .awaitSingle()
    }

    suspend fun loadHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        val lastSeqId = sr.queryParam("lastSeqId").map { it.toLong() }.orElse(null)
        val pageSize = sr.queryParam("pageSize").map { it.toInt() }.orElse(null)
        return dispatchingCenter.findCustomer(userId)
            .flatMap {
                messageService.loadHistoryMessage(it.organizationId, it.userId!!, lastSeqId, pageSize)
            }
            .flatMap { ok().bodyValue(it) }
            .awaitSingle()
    }
}