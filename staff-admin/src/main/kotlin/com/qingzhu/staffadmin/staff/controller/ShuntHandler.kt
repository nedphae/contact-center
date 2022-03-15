package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.bodyToMonoWithOrg
import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import com.qingzhu.staffadmin.staff.service.ShuntService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.kotlin.core.publisher.toMono

@RestController
class ShuntHandler(
    private val reactiveShuntRepository: ReactiveShuntRepository,
    private val shuntService: ShuntService,
) {
    suspend fun findFirstByCode(sr: ServerRequest): ServerResponse {
        val code = sr.pathVariable("code")
        val shuntDto = shuntService.findFirstByCode(code).toMono()
        return ok().body(shuntDto).awaitSingle()
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
        val body = sr.bodyToMonoWithOrg<Shunt>()
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
        val body = sr.bodyToMonoWithOrg<ShuntClass>()
            .flatMap { shuntService.saveShuntClass(it) }
        return ok().body(body).awaitSingle()
    }

    suspend fun deleteAllByIds(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        val ids = sr.bodyToFlux<Long>()
        return shuntService.deleteAllByIds(organizationId!!, ids)
            .then(ok().build()).awaitSingle()
    }
}