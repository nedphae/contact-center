package com.qingzhu.dispatcher.controller

import com.qingzhu.dispatcher.domain.dto.ConversationView
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
                assignmentService.assignmentStaff(oi, uid)
                        .transform { ok().body(it) }
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun assignmentAuto(sr: ServerRequest): ServerResponse {
        val response = ok().build()
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("uid").map(String::toLong).map { uid ->
                ok().body(assignmentService.assignmentAuto(oi, uid))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

}