package com.qingzhu.common.util

import com.qingzhu.common.security.awaitPrincipalTriple
import org.springframework.web.reactive.function.server.ServerRequest

suspend fun ServerRequest.awaitGetOrganizationId(): Triple<Int?, Long?, String?> {
    val (jwtOrganizationId, jwtStaffId, jwtStaffName) = this.awaitPrincipalTriple()
    return Triple(
        this.queryParam("organizationId").map(String::toInt).orElse(jwtOrganizationId),
        this.queryParam("staffId").map(String::toLong).orElse(jwtStaffId),
        this.queryParam("staffName").orElse(jwtStaffName))
}
