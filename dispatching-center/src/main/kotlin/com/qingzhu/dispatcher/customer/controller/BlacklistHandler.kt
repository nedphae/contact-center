package com.qingzhu.dispatcher.customer.controller

import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.dispatcher.customer.domain.constant.PreventStrategy
import com.qingzhu.dispatcher.customer.domain.entity.Blacklist
import com.qingzhu.dispatcher.customer.service.BlacklistService
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@RestController
class BlacklistHandler(
    private val blacklistService: BlacklistService,
) {
    suspend fun saveBlacklist(sr: ServerRequest): ServerResponse {
        val (orgId, staffId) = sr.awaitGetOrganizationId()
        val blacklistFlow = sr.bodyToFlow<Blacklist>()
        val listWithOrg = blacklistFlow.onEach { it.organizationId = orgId; it.staffId = staffId }
        val result = blacklistService.saveBlacklist(listWithOrg)
        return ServerResponse.ok().bodyAndAwait(result)
    }

    suspend fun getAllBlacklist(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        val audited = sr.queryParamOrNull("audited")?.toBoolean()
        return ServerResponse.ok().body(blacklistService.getAllBlacklist(orgId!!, audited)).awaitSingle()
    }

    suspend fun getBlacklistBy(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        val result = sr.queryParam("preventStrategy")
            .map { preventStrategy ->
                sr.queryParam("preventSource")
                    .map { preventSource ->
                        Blacklist(PreventStrategy.valueOf(preventStrategy), preventSource)
                            .also { it.organizationId = orgId }
                    }
                    .map {
                        blacklistService.getBlacklistBy(it)
                    }
                    .orElse(Mono.empty())
            }
            .orElse(Mono.empty())
        return ServerResponse.ok().body(result).awaitSingle()
    }

    suspend fun remove(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        val blacklistFlow = sr.bodyToFlow<Blacklist>()
        val result = blacklistService.remove(orgId!!, blacklistFlow)
        return ServerResponse.ok().bodyValueAndAwait(result)
    }
}