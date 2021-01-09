package com.qingzhu.bot.knowledgebase.service

import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Deprecated("仅测试使用，后期需要删除")
@Service
class AuthProvider(private val webClient: WebClient) {

    fun getStaffInfo(organizationId: Int, staffId: Int): String {
        return webClient.get()
                .uri("http://staff-admin/staff/info?organizationId=9491&staffId=1")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction
                        .clientRegistrationId("authProvider"))
                .retrieve()
                .bodyToMono<String>()
                .blockOptional().get()
    }
}