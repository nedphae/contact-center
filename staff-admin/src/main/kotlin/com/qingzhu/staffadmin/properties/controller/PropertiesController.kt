package com.qingzhu.staffadmin.properties.controller

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.service.PropertiesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/props")
class PropertiesController(
        val propertiesService: PropertiesService
) {
    @GetMapping
    fun getAllProperties(): String {
        return propertiesService.getAllProperties().unsafeRunSync()
    }

    @RequestMapping(method = [RequestMethod.PUT, RequestMethod.POST])
    fun updateProperties(@RequestBody properties: List<Properties>): List<Properties> {
        return propertiesService.saveAll(properties).unsafeRunSync()
    }
}