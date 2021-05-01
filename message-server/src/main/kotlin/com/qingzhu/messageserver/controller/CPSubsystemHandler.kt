package com.qingzhu.messageserver.controller

import com.qingzhu.messageserver.service.CPSubsystemService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@RestController
class CPSubsystemHandler(
    private val cpSubsystemService: CPSubsystemService
) {
    suspend fun getBotLock(sr: ServerRequest): ServerResponse {
        val lockOrNot = cpSubsystemService.getBotLock()
        return ServerResponse.ok().bodyValue(lockOrNot).awaitSingle()
    }

    suspend fun releaseBotLock(sr: ServerRequest): ServerResponse {
        return try {
            cpSubsystemService.releaseBotLock()
            ServerResponse.ok().build().awaitSingle()
        } catch (ex: Exception) {
            ServerResponse.badRequest().build().awaitSingle()
        }
    }
}