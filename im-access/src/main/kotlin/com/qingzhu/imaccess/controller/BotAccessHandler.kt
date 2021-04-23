package com.qingzhu.imaccess.controller

import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.service.BotAccessService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@RestController
class BotAccessHandler(
        private val botAccessService: BotAccessService,
) {
    /**
     *  用户注册
     */
    suspend fun register(sr: ServerRequest): ServerResponse {
        val customerConfig = sr.awaitBody<CustomerConfig>()
        val view = botAccessService.register(customerConfig)
        return ok().bodyValueAndAwait(view)
    }
}