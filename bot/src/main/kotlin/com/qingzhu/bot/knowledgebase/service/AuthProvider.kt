package com.qingzhu.bot.knowledgebase.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@Deprecated("api 改动，后期需要删除")
@Service
@FeignClient(name = "staff-admin")
interface AuthProvider {
    @PostMapping(value = ["/staff"])
    fun registerCustomerService(@RequestParam("username") username: String,
                                @RequestParam("password") password: String,
                                @RequestParam("role") role: String)
}