package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.awaitPrincipalTriple
import com.qingzhu.common.security.awaitPrincipalTripleWithBodyOrg
import com.qingzhu.staffadmin.staff.domain.entity.ShuntUIConfig
import com.qingzhu.staffadmin.staff.repository.ShuntUIConfigRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class ShuntUIConfigHandler(
    private val shuntUIConfigRepository: ShuntUIConfigRepository
) {
    /**
     * 通过 接待组获取 界面配置
     */
    suspend fun getUIConfigByShunt(sr: ServerRequest): ServerResponse {
        val (orgId, _, _) = sr.awaitPrincipalTriple()
        val shuntId = sr.queryParamOrNull("shuntId")?.toLong()
        var response = ok().build().awaitSingle()
        if (orgId != null) {
            if (shuntId != null) {
                val config = shuntUIConfigRepository.findByOrganizationIdAndShuntId(orgId, shuntId).awaitSingle()
                if (config != null) {
                    response = ok().bodyValueAndAwait(config)
                }
            } else {
                val list = shuntUIConfigRepository.findByOrganizationId(orgId)
                response = ok().bodyAndAwait(list)
            }

        }
        return response
    }

    suspend fun saveUIConfig(sr: ServerRequest): ServerResponse {
        var uiConfig = sr.awaitPrincipalTripleWithBodyOrg<ShuntUIConfig>()
        return if (uiConfig != null) {
            uiConfig = shuntUIConfigRepository.save(uiConfig).awaitSingle()
            ok().bodyValueAndAwait(uiConfig)
        } else badRequest().build().awaitSingle()
    }
}