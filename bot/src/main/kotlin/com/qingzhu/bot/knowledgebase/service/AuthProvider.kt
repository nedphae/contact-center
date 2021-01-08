package com.qingzhu.bot.knowledgebase.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Deprecated("仅测试使用，后期需要删除")
@Service
class AuthProvider(private val webClient: WebClient) {

    fun getStaffInfo(organizationId: Int, staffId: Int): String {
        return webClient.get()
                .uri("http://staff-admin/staff/info")
                .attribute("organizationId", organizationId)
                .attribute("staffId", staffId)
                .retrieve()
                .bodyToMono<String>().blockOptional().get()
    }
}