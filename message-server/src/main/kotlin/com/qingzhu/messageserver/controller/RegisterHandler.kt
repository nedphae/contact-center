package com.qingzhu.messageserver.controller

import com.qingzhu.messageserver.domain.dto.CustomerBaseStatusDto
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import com.qingzhu.messageserver.domain.entity.StaffStatus
import com.qingzhu.messageserver.service.CustomerStatusService
import com.qingzhu.messageserver.service.StaffStatusService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.accepted

@RestController
class RegisterHandler(
        private val customerStatusService: CustomerStatusService,
        private val staffStatusService: StaffStatusService
) {

    suspend fun registerCustomer(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerStatus>()
                .map { customerStatusService.saveStatus(it) }
                .flatMap { accepted().build() }.awaitSingle()
    }

    suspend fun registerStaff(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<StaffStatus>()
                .map { staffStatusService.saveStatus(it) }
                .flatMap { accepted().build() }.awaitSingle()
    }

    suspend fun unregisterCustomer(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerBaseStatusDto>()
                .doOnNext {
                    customerStatusService.setStatusOffline(it)
                    // TODO: 通知调度中心 关闭会话 重新调度
                }
                .flatMap { accepted().build() }.awaitSingle()
    }

    suspend fun unregisterStaff(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<StaffChangeStatusDto>()
                .map { staffStatusService.setStatusOffline(it) }.flatMap { accepted().build() }.awaitSingle()
    }
}