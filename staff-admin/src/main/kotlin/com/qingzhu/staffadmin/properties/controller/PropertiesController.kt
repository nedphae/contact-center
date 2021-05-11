package com.qingzhu.staffadmin.properties.controller

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.service.PropertiesService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/config/props")
class PropertiesController(
    val propertiesService: PropertiesService
) {
    @GetMapping
    fun getAllProperties(): Mono<String> {
        return propertiesService.getAllProperties().unsafeRunSync()
    }

    @RequestMapping(method = [RequestMethod.PUT, RequestMethod.POST])
    fun updateProperties(@RequestBody properties: List<Properties>): Flux<Properties> {
        return propertiesService.saveAll(properties).unsafeRunSync()
    }
}