package com.qingzhu.messageserver.controller

import com.qingzhu.common.security.awaitPrincipalTriple
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
import reactor.core.publisher.Mono

val emptyResponse = ok().build()

private suspend fun ServerRequest.getOrganizationIdAndUserId(
    queryParamName: String,
    getBody: (oid: Int, queryParam: String) -> Mono<ServerResponse>
): ServerResponse {
    val (organizationId, _, _) = this.awaitPrincipalTriple()
    val queryParamOrganizationId = this
        .queryParam("organizationId")
        .map(String::toInt)
        .orElseGet {
            organizationId
        }
    return this.queryParam(queryParamName)
        .map {
            getBody(queryParamOrganizationId, it)
        }
        .orElse(emptyResponse)
        .awaitSingle()
}

@RestController
class StaffStatusHandler(
    private val staffStatusService: StaffStatusService
) {
    suspend fun findIdleStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        val result = sr.queryParam("shuntId").map(String::toLong).map { rg ->
            staffStatusService.findIdleStaffWithStaffDispatcherDto(organizationId!!, rg)
        }.orElse(emptyFlow())

        return ok().bodyAndAwait(result)
    }

    suspend fun findBotStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
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
        val (oid, _, _) = sr.awaitPrincipalTriple()
        return (if (oid != null) {
            val result = staffStatusService.findAllOnlineStaff(oid)
            ok().bodyValue(result)
        } else notFound().build()).awaitSingle()
    }
}

@RestController
class CustomerStatusHandler(private val customerStatusService: CustomerStatusService) {

    suspend fun findByUid(sr: ServerRequest): ServerResponse {
        return sr.getOrganizationIdAndUserId("uid") { oid, uid ->
            ok().body(customerStatusService.findByUid(oid, uid))
        }
    }

    suspend fun findByUserId(sr: ServerRequest): ServerResponse {
        return sr.getOrganizationIdAndUserId("userId") { oid, userId ->
            ok().body(customerStatusService.findByUserId(oid, userId.toLong()))
        }
    }

    suspend fun findAllOnlineCustomer(sr: ServerRequest): ServerResponse {
        val (oid, _, _) = sr.awaitPrincipalTriple()
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
        return sr.getOrganizationIdAndUserId("userId") { oid, userId ->
            ok().body(conversationStatusService.findByUserId(oid, userId.toLong()))
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