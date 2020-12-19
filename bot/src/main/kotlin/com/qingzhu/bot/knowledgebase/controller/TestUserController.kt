package com.qingzhu.bot.knowledgebase.controller

import com.qingzhu.bot.knowledgebase.service.AuthProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Deprecated("api 改动，后期需要删除")
@RestController
@RequestMapping("/users/test")
class TestUserController {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @PostMapping
    fun registerCustomerService(username: String, password: String, role: String) {
        authProvider.registerCustomerService(username, password, role)
    }
}