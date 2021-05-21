package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.service.QuickReplyService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@RestController
class QuickRecoveryHandler(
    private val quickReplyService: QuickReplyService,
) {
    suspend fun findQuickRecoveryByStaff(sr: ServerRequest): ServerResponse {
        return sr
            .principal()
            .getPrincipalTriple()
            .flatMap { (organizationId, staffId, _) ->
                organizationId.flatMap {
                    staffId.flatMap { sid ->
                        quickReplyService.findQuickReplyByStaff(it, sid)
                    }
                }
            }
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun findQuickRecoveryByOrganizationId(sr: ServerRequest): ServerResponse {
        return sr
            .principal()
            .getPrincipalTriple()
            .flatMap { (organizationId, _, _) ->
                organizationId.flatMap {
                    quickReplyService.findQuickReplyByOrganizationId(it)
                }
            }
            .transform { ok().body(it) }
            .awaitSingle()
    }
}