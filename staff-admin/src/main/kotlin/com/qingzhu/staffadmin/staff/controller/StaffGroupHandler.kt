package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.bodyToMonoWithOrg
import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.service.StaffGroupService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class StaffGroupHandler(private val staffGroupService: StaffGroupService) {
    suspend fun findAllGroup(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { staffGroupService.findAllGroup(it.t1) }
                .asFlow()
        )
    }

    suspend fun saveGroup(sr: ServerRequest): ServerResponse {
        val body = sr.bodyToMonoWithOrg<StaffGroup>()
            .flatMap { staffGroupService.saveGroup(it) }
        return ok().body(body).awaitSingle()
    }

    suspend fun deleteAllByIds(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        val ids = sr.bodyToFlux<Long>()
        return staffGroupService.deleteAllByIds(organizationId!!, ids)
            .then(ok().build()).awaitSingle()
    }
}