package com.qingzhu.bot.knowledgebase.controller

import com.qingzhu.bot.knowledgebase.service.AuthProvider
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body

@Deprecated("api 改动，后期需要删除")
@RestController
class TestUserHandler {
    @Autowired
    private lateinit var authProvider: AuthProvider

    suspend fun getStaff(sr: ServerRequest): ServerResponse {
        return authProvider.getStaffInfo(9491, 1)
            .transform { ServerResponse.ok().body(it) }
            .awaitSingle()
    }

    suspend fun getStaffTest(sr: ServerRequest): ServerResponse {
        return authProvider.getStaffInfoTest(9491, 1)
            .transform { ServerResponse.ok().body(it) }
            .awaitSingle()
    }

}