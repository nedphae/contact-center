package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.service.StaffGroupService
import kotlinx.coroutines.reactive.asFlow
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait

@RestController
class StaffGroupHandler(private val staffGroupService: StaffGroupService) {
    suspend fun findAllGroup(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { (oid, _, _) -> oid.flatMapMany { staffGroupService.findAllGroup(it) } }
                .asFlow()
        )
    }
}