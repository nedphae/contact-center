package com.qingzhu.imaccess.socketio.authorization

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.HandshakeData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component

/**
 * WebSocket JWT 认证
 */
@Component
class JWTAuthorizationListener(
    @Qualifier("reactiveJwtDecoderBean") val reactiveJwtDecoder: ReactiveJwtDecoder
) : AuthorizationListener {

    private val logger = LoggerFactory.getLogger(JWTAuthorizationListener::class.java)

    override fun isAuthorized(data: HandshakeData?): Boolean {
        val token = data?.getSingleUrlParam("token")
        return try {
            var verify = false
            // 过滤用户 命名空间 不做验证
            if (token == "customer") {
                verify = true
            } else {
                reactiveJwtDecoder.decode(token).subscribe { jwt ->
                    // jwt 信息添加到 HandshakeData
                    val roleList = jwt.getClaimAsStringList("authorities")
                    val organizationId = jwt.getClaimAsString("oid")
                    val staffId = jwt.getClaimAsString("sid")
                    data?.urlParams?.set("role", roleList)
                    data?.urlParams?.set("oid", listOf(organizationId))
                    data?.urlParams?.set("sid", listOf(staffId))
                    verify = true
                }
            }
            verify
        } catch (jwe: JwtException) {
            logger.error("jwt decode error:", jwe)
            false
        }

    }

}