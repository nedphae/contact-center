package com.qingzhu.staffadmin.properties.controller

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.service.PropertiesService
import kotlinx.coroutines.flow.Flow
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/config/props")
class PropertiesController(
    val propertiesService: PropertiesService
) {
    @GetMapping
    fun getAllProperties(principal: Principal): Mono<String> {
        val orgId = (principal as JwtAuthenticationToken).token.getClaim<Long>("oid").toInt()
        return propertiesService.getAllProperties(orgId).unsafeRunSync()
    }

    @GetMapping("/by-key")
    fun findDistinctTopByKey(organizationId: Int, key: String): Mono<Properties> {
        return propertiesService.findDistinctTopByKey(organizationId, key)
    }

    @RequestMapping(method = [RequestMethod.PUT, RequestMethod.POST])
    suspend fun updateProperties(@RequestBody properties: List<Properties>): Flow<Properties> {
        return propertiesService.saveAll(properties)
    }
}