package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.staff.domain.entity.Shunt
import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import com.qingzhu.staffadmin.staff.service.ShuntService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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

    suspend fun saveShunt(sr: ServerRequest): ServerResponse {
        val body = sr.bodyToMono<Shunt>()
            .flatMap {
                sr.principal().getPrincipalTriple()
                    .flatMap { (organizationId, _) ->
                        organizationId.map { oid ->
                            it.organizationId = oid
                            it
                        }
                    }
            }
            .flatMap {
                shuntService.saveShunt(it)
            }
        return ok().body(body).awaitSingle()
    }

    suspend fun findAllShuntClass(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { (oid, _, _) -> oid.flatMapMany { shuntService.findAllShuntClass(it) } }
                .asFlow()
        )
    }

    suspend fun saveShuntClass(sr: ServerRequest): ServerResponse {
        val body = sr.bodyToMono<ShuntClass>()
            .flatMap {
                sr.principal().getPrincipalTriple()
                    .flatMap { (organizationId, _) ->
                        organizationId.map { oid ->
                            it.organizationId = oid
                            it
                        }
                    }
            }
            .flatMap {
                shuntService.saveShuntClass(it)
            }
        return ok().body(body).awaitSingle()
    }
}