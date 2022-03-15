package com.qingzhu.common.util

import com.qingzhu.common.security.awaitPrincipalTriple
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

suspend fun ServerRequest.awaitGetOrganizationId(): Triple<Int?, Long?, String?> {
    val (jwtOrganizationId, jwtStaffId, jwtStaffName) = this.awaitPrincipalTriple()
    return Triple(
        this.queryParam("organizationId").map(String::toInt).orElse(jwtOrganizationId),
        this.queryParam("staffId").map(String::toLong).orElse(jwtStaffId),
        this.queryParam("staffName").orElse(jwtStaffName)
    )
}

val emptyResponse = ServerResponse.ok().build()
suspend fun ServerRequest.getOrganizationIdAnd(
    queryParamName: String,
    getBody: (oid: Int, queryParam: String) -> Mono<ServerResponse>
): ServerResponse {
    val (organizationId, _, _) = this.awaitGetOrganizationId()
    return this.queryParam(queryParamName)
        .map {
            getBody(organizationId!!, it)
        }
        .orElse(emptyResponse)
        .awaitSingle()
}
