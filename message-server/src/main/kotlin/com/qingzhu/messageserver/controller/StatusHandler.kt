package com.qingzhu.messageserver.controller

import com.qingzhu.common.security.awaitGetPrincipalTriple
import com.qingzhu.common.util.getOrgAnd
import com.qingzhu.messageserver.domain.dto.CustomerBaseClientDto
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.service.ConversationStatusService
import com.qingzhu.messageserver.service.CustomerStatusService
import com.qingzhu.messageserver.service.StaffStatusService
import kotlinx.coroutines.flow.emptyFlow
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
            }.orElse(emptyFlow())
        }.orElse(emptyFlow())
        return ok().bodyAndAwait(result)
    }

    suspend fun findBotStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val result = sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("shuntId").map(String::toLong).map { rg ->
                staffStatusService.findBotStaffWithStaffDispatcherDto(oi, rg)
            }.orElse(emptyFlow())
        }.orElse(emptyFlow())
        return ok().bodyAndAwait(result)
    }

    suspend fun staffAssignment(sr: ServerRequest): ServerResponse {
        val param = sr.bodyToMono<StaffChangeStatusDto>()
        return param.flatMap { staffStatusService.assignment(it) }
            .flatMap {
                accepted().build()
            }
            .switchIfEmpty(status(HttpStatus.NOT_ACCEPTABLE).build())
            .awaitSingle()
    }

    suspend fun findAllOnlineStaff(sr: ServerRequest): ServerResponse {
        val (oid, sid, name) = sr.awaitGetPrincipalTriple()
        return (if (oid != null) {
            val result = staffStatusService.findAllOnlineStaff(oid)
            ok().bodyValue(result)
        } else notFound().build()).awaitSingle()
    }
}

@RestController
class CustomerStatusHandler(private val customerStatusService: CustomerStatusService) {
    val response = ok().build()

    suspend fun findStaffIdOrShuntId(sr: ServerRequest): ServerResponse {
        return sr.getOrgAnd("usedId") { oi, uid ->
            ok().body(customerStatusService.findStaffIdOrShuntId(oi, uid.toLong()))
        }
    }

    suspend fun findByUid(sr: ServerRequest): ServerResponse {
        return sr.getOrgAnd("usedId") { oi, uid ->
            customerStatusService.findByUid(oi, uid)
                .transform { ok().body(it) }
        }
    }

    suspend fun findByUserId(sr: ServerRequest): ServerResponse {
        return sr.getOrgAnd("usedId") { oi, uid ->
            customerStatusService.findByUserId(oi, uid.toLong())
                .transform { ok().body(it) }
        }
    }

    suspend fun findAllOnlineCustomer(sr: ServerRequest): ServerResponse {
        val (oid, sid, name) = sr.awaitGetPrincipalTriple()
        return (if (oid != null) {
            val result = customerStatusService.findAllOnlineCustomer(oid)
            ok().bodyValue(result)
        } else notFound().build()).awaitSingle()
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
        return sr.getOrgAnd("usedId") { oi, uid ->
            conversationStatusService.findByUserId(oi, uid.toLong())
                .transform { ok().body(it) }
        }
    }

    suspend fun new(sr: ServerRequest): ServerResponse {
        // 测试 json 序列化问题
        // val json = sr.awaitBody<String>()
        // println(json)
        // val body = JsonUtils.fromJson(json)
        // println(body)
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