package com.qingzhu.common.util

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

suspend fun ServerRequest.getOrgAnd(sid: String = "staffId", body: (oid: Int, sid: String) -> Mono<ServerResponse>): ServerResponse {
    val response = ServerResponse.ok().build()
    return this.queryParam("organizationId").map(String::toInt).map { oid ->
        this.queryParam(sid).map { uid ->
            body(oid, uid)
        }.orElse(response)
    }.orElse(response).awaitSingle()
}
