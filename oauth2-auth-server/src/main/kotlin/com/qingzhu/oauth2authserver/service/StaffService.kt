package com.qingzhu.oauth2authserver.service

import com.qingzhu.common.security.AuthorizedFeignClient
import com.qingzhu.oauth2authserver.domain.dto.InnerUser
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Service
@AuthorizedFeignClient(name = "staff-admin")
interface StaffService {

    @GetMapping(value = ["/staff"])
    fun findFirstByUsername(@RequestParam("organizationId") organizationId: Long,
                            @RequestParam("username") username: String?): InnerUser?
}