package com.qingzhu.bot.knowledgebase.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Deprecated("api 改动，后期需要删除")
@Service
@AuthorizedFeignClient(name = "staff-admin")
interface AuthProvider {
    @GetMapping(value = ["/staff/info"])
    fun getStaffInfo(@RequestParam("organizationId") organizationId: Int,
                                @RequestParam("staffId") staffId: Int): String?
}