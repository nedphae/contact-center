package com.qingzhu.messageserver.controller

import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.service.CustomerStatusService
import com.qingzhu.messageserver.service.StaffStatusService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@RestController
class StatusHandler(
        private val staffStatusService: StaffStatusService,
        private val customerStatusService: CustomerStatusService
) {
    suspend fun findIdleStaffWithStaffDispatcherDto(sr: ServerRequest): ServerResponse {
        val result = sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("shuntId").map(String::toLong).map { rg ->
                staffStatusService.findIdleStaffWithStaffDispatcherDto(oi, rg)
            }.orElse(listOf())
        }.orElse(listOf()).asFlow()
        return ok().bodyAndAwait(result)
    }

    suspend fun assignmentStaff(sr: ServerRequest): ServerResponse {
        val param = sr.bodyToMono<StaffChangeStatusDto>()
        return param.flatMap { staffStatusService.assignmentCustomer(it) }
                .flatMap { param.flatMap { customerStatusService.assignmentStaff(it) } }
                .flatMap {
                    accepted().build()
                }
                .switchIfEmpty(status(HttpStatus.FORBIDDEN).build())
                .awaitSingle()
    }

    suspend fun findStaffIdOrShuntId(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("userId").map(String::toLong).map { uid ->
                ok().body(customerStatusService.findStaffIdOrShuntId(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun checkIsStaffService(sr: ServerRequest): ServerResponse {
        val response = ok().bodyValue(false)
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("uid").map { uid ->
                ok().body(customerStatusService.checkIsStaffService(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }
}