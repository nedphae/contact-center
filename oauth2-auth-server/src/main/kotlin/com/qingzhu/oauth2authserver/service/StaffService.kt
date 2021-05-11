package com.qingzhu.oauth2authserver.service

import com.qingzhu.oauth2authserver.domain.dto.InnerUser
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Service
@FeignClient(name = "staff-admin")
interface StaffService {

    @GetMapping(value = ["/staff"])
    fun findFirstByUsername(
        @RequestParam("organizationId") organizationId: Int,
        @RequestParam("username") username: String?
    ): InnerUser?
}