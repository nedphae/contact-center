package com.qingzhu.oauth2authserver.security

import org.springframework.boot.context.properties.ConfigurationProperties


// 不使用 ConstructorBinding
// 必须设置变量为可变 var
@ConfigurationProperties(prefix = "im.security.inner-oauth")
data class InnerLoadBalanceProperties(
    var enable: Boolean = false,
    var clientAuthorization: ClientAuthorization = ClientAuthorization()
) {
    data class ClientAuthorization(
        var accessTokenUri: String = "http://oauth2-auth-server/oauth/token",
        var tokenServiceId: String = "oauth2-auth-server",
        var clientId: String = "inner_client",
        var clientSecret: String = "test_secret"
    )
}

