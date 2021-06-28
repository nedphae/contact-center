package com.qingzhu.staffadmin.properties.controller

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.service.PropertiesService
import org.springframework.security.oauth2.jwt.Jwt
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
        val orgId = (principal as Jwt).getClaim<Int>("oid")
        return propertiesService.getAllProperties(orgId).unsafeRunSync()
    }

    @RequestMapping(method = [RequestMethod.PUT, RequestMethod.POST])
    fun updateProperties(@RequestBody properties: List<Properties>): Flux<Properties> {
        return propertiesService.saveAll(properties).unsafeRunSync()
    }
}