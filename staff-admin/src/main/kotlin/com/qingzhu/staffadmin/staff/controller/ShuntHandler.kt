package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.security.setOrganizationId
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import com.qingzhu.staffadmin.staff.service.ShuntService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class ShuntHandler(
    private val reactiveShuntRepository: ReactiveShuntRepository,
    private val shuntService: ShuntService,
) {
    suspend fun findFirstByCode(sr: ServerRequest): ServerResponse {
        val code = sr.pathVariable("code")
        return ok().body(reactiveShuntRepository.findFirstByCode(code)).awaitSingle()
    }

    suspend fun findById(sr: ServerRequest): ServerResponse {
        val id = sr.pathVariable("id").toLong()
        return ok().body(reactiveShuntRepository.findById(id)).awaitSingle()
    }

    suspend fun findAllShunt(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { shuntService.findAllShunt(it.t1) }
                .asFlow()
        )
    }

    suspend fun saveShunt(sr: ServerRequest): ServerResponse {
        val body = sr.bodyToMono<Shunt>()
            .flatMap { sr.principal().setOrganizationId(it) }
            .flatMap { shuntService.saveShunt(it) }
        return ok().body(body).awaitSingle()
    }

    suspend fun findAllShuntClass(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { shuntService.findAllShuntClass(it.t1) }
                .asFlow()
        )
    }

    suspend fun saveShuntClass(sr: ServerRequest): ServerResponse {
        val body = sr.bodyToMono<ShuntClass>()
            .flatMap { sr.principal().setOrganizationId(it) }
            .flatMap { shuntService.saveShuntClass(it) }
        return ok().body(body).awaitSingle()
    }
}