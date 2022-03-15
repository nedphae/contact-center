package com.qingzhu.messageserver.controller

import com.qingzhu.common.security.bodyToMonoWithOrgAndStaff
import com.qingzhu.messageserver.domain.dto.*
import com.qingzhu.messageserver.service.CustomerStatusService
import com.qingzhu.messageserver.service.StaffStatusService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.accepted
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono

@RestController
class RegisterHandler(
    private val customerStatusService: CustomerStatusService,
    private val staffStatusService: StaffStatusService
) {

    suspend fun registerCustomer(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerStatusDto>()
            .map { customerStatusService.saveStatus(it.toCustomerStatus()) }
            .flatMap { accepted().bodyValue(it) }.awaitSingle()
    }

    suspend fun registerStaff(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<StaffStatusDto>()
            .flatMap { staffStatusService.registerOrUpdateStaff(it) }
            .flatMap { accepted().bodyValue(it) }.awaitSingle()
    }

    suspend fun unregisterCustomer(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<CustomerBaseStatusDto>()
            .flatMap {
                customerStatusService.setStatusOffline(it)
                // 通知调度中心 关闭会话 重新调度, 由接入服务器进行
            }
            .flatMap { accepted().bodyValue(it) }.awaitSingle()
    }

    suspend fun unregisterStaff(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<StaffChangeStatusDto>()
            .map { staffStatusService.setStatusOffline(it) }.flatMap { accepted().build() }.awaitSingle()
    }

    suspend fun updateStaffStatus(sr: ServerRequest): ServerResponse {
        return sr.bodyToMonoWithOrgAndStaff<UpdateStaffStatus>()
            .flatMap {
                ok().body(staffStatusService.updateStaffStatus(it))
            }.awaitSingle()
    }
}