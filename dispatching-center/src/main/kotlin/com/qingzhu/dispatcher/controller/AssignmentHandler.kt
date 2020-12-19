package com.qingzhu.dispatcher.controller

import com.qingzhu.dispatcher.service.AssignmentService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class AssignmentHandler(
        private val assignmentService: AssignmentService
) {
    suspend fun assignmentStaff(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("userId").map(String::toLong).map { uid ->
                ok().body(assignmentService.assignmentStaff(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun checkIsStaffService(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("uid").map { uid ->
                ok().body(assignmentService.checkIsStaffService(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

}