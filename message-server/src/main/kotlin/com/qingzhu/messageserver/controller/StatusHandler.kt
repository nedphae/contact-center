package com.qingzhu.messageserver.controller

import com.qingzhu.messageserver.domain.dto.CustomerBaseClientDto
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.dto.StaffDispatcherDto
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.service.ConversationStatusService
import com.qingzhu.messageserver.service.CustomerStatusService
import com.qingzhu.messageserver.service.StaffStatusService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@RestController
class StaffStatusHandler(
        private val staffStatusService: StaffStatusService
) {
    suspend fun findIdleStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val result = sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("shuntId").map(String::toLong).map { rg ->
                staffStatusService.findIdleStaffWithStaffDispatcherDto(oi, rg)
            }.orElse(listOf<StaffDispatcherDto>().asFlow())
        }.orElse(listOf<StaffDispatcherDto>().asFlow())
        return ok().bodyAndAwait(result)
    }

    suspend fun findBotStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val result = sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("shuntId").map(String::toLong).map { rg ->
                staffStatusService.findBotStaffWithStaffDispatcherDto(oi, rg)
            }.orElse(listOf<StaffDispatcherDto>().asFlow())
        }.orElse(listOf<StaffDispatcherDto>().asFlow())
        return ok().bodyAndAwait(result)
    }

    suspend fun staffAssignment(sr: ServerRequest): ServerResponse {
        val param = sr.bodyToMono<StaffChangeStatusDto>()
        return param.flatMap { staffStatusService.assignment(it) }
                .flatMap {
                    accepted().build()
                }
                .switchIfEmpty(status(HttpStatus.FORBIDDEN).build())
                .awaitSingle()
    }
}

@RestController
class CustomerStatusHandler(private val customerStatusService: CustomerStatusService) {

    suspend fun findStaffIdOrShuntId(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("userId").map(String::toLong).map { uid ->
                ok().body(customerStatusService.findStaffIdOrShuntId(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun findByUid(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("uid").map { uid ->
                customerStatusService.findByUid(oi, uid)
                        .transform { ok().body(it) }
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    /**
     * 协程实现的命令式
     */
    suspend fun updateByClientId(sr: ServerRequest): ServerResponse {
        val customerBaseClientDto = sr.awaitBody<CustomerBaseClientDto>()
        val updatedStatus = customerStatusService.updateByClientId(customerBaseClientDto).awaitSingle()
        return if (updatedStatus != null) {
             ok().bodyValueAndAwait(updatedStatus)
        } else {
            status(HttpStatus.NOT_FOUND).buildAndAwait()
        }
    }

}

@RestController
class ConversationStatusHandler(private val conversationStatusService: ConversationStatusService) {

    suspend fun findByUserId(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("userId").map(String::toLong).map { uid ->
                conversationStatusService.findByUserId(oi, uid)
                        .transform { ok().body(it) }
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun new(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<ConversationStatus>()
                .flatMap { conversationStatusService.generate(it) }
                .transform {
                    ok().body(it)
                }.awaitSingle()
    }

    suspend fun end(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<ConversationStatus>()
                .flatMap { conversationStatusService.endConversation(it) }
                .transform { ok().build() }.awaitSingle()
    }
}