package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.bodyToMonoWithOrgAndStaff
import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.domain.entity.QuickReply
import com.qingzhu.staffadmin.staff.domain.entity.QuickReplyGroup
import com.qingzhu.staffadmin.staff.service.QuickReplyService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@RestController
class QuickReplyHandler(
    private val quickReplyService: QuickReplyService,
) {
    suspend fun findQuickReplyByStaff(sr: ServerRequest): ServerResponse {
        return sr
            .principal()
            .getPrincipalTriple()
            .flatMap { quickReplyService.findQuickReplyByStaff(it.t1, it.t2) }
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun findQuickReplyByOrganizationId(sr: ServerRequest): ServerResponse {
        return sr
            .principal()
            .getPrincipalTriple()
            .flatMap { quickReplyService.findQuickReplyByOrganizationId(it.t1) }
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun saveQuickReply(sr: ServerRequest): ServerResponse {
        return sr.bodyToMonoWithOrgAndStaff<QuickReply>()
            .flatMap { quickReplyService.saveQuickReply(it) }
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun saveQuickReplyGroup(sr: ServerRequest): ServerResponse {
        return sr.bodyToMonoWithOrgAndStaff<QuickReplyGroup>()
            .flatMap { quickReplyService.saveQuickReplyGroup(it) }
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun deleteQuickReply(sr: ServerRequest): ServerResponse {
        val id = sr.pathVariable("id").toLong()
        return quickReplyService.deleteQuickReply(id).then(
            ok().build()
        ).awaitSingle()
    }

    suspend fun deleteQuickReplyGroup(sr: ServerRequest): ServerResponse {
        val id = sr.pathVariable("id").toLong()
        return quickReplyService.deleteQuickReplyGroup(id).then(
            ok().build()
        ).awaitSingle()
    }
}