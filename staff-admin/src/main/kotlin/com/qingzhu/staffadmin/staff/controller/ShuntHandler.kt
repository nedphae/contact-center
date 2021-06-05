package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import com.qingzhu.staffadmin.staff.service.ShuntService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyAndAwait

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
                .flatMapMany { (oid, _, _) -> oid.flatMapMany { shuntService.findAllShunt(it) } }
                .asFlow()
        )
    }
}