package com.qingzhu.dispatcher.controller

import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.dispatcher.domain.dto.StaffStatusDto
import com.qingzhu.dispatcher.service.AssignmentService
import com.qingzhu.dispatcher.service.QueueService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import java.util.*

@RestController
class AssignmentHandler(
    private val assignmentService: AssignmentService,
    private val queueService: QueueService,
) {
    val response = ok().build()

    suspend fun assignmentStaff(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        return Optional.ofNullable(organizationId).map { oi ->
            sr.queryParam("userId").map(String::toLong).map { uid ->
                assignmentService.assignmentStaff(oi, uid)
                    .transform { ok().body(it) }
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun assignmentAuto(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        return Optional.ofNullable(organizationId).map { oid ->
            sr.queryParam("userId").map(String::toLong).map { userId ->
                ok().body(assignmentService.assignmentAuto(oid, userId))
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun assignmentFromQueue(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        return Optional.ofNullable(organizationId).map { oid ->
            sr.queryParam("userId").map(String::toLong).map { userId ->
                queueService.removeUser(oid, userId).then(response)
            }.orElse(response)
        }.orElse(response).awaitSingle()
    }

    suspend fun assignmentFromQueueForStaff(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<StaffStatusDto>()
            .flatMap {
                queueService.assignmentFromQueue(it).then(ok().build())
            }
            .awaitSingle()
    }

}