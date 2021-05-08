package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.staffadmin.staff.repository.ReactiveShuntRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@RestController
class ShuntHandler(
    private val reactiveShuntRepository: ReactiveShuntRepository
) {
    suspend fun findFirstByCode(sr : ServerRequest) : ServerResponse{
        val code = sr.pathVariable("code")
        return ok().body(reactiveShuntRepository.findFirstByCode(code)).awaitSingle()
    }
}