package com.qingzhu.common.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * Utility class for Spring WebFlux Security.
 */
object SecurityUtils {

    /**
     * 获取当前登录的用户名称，仅支持在 *WebFlux* 中调用 / 同一线程内调用
     * @see ReactiveSecurityContextHolder
     * @throws IllegalStateException 没有获取到 SecurityContext
     */
    fun getCurrentUserLogin(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext {
                    SecurityContextHolder.getContext().authentication = it
                }
                .flatMap { Mono.justOrEmpty(extractPrincipal(it)) }
                .switchIfEmpty(Mono.just("admin"))
    }

    /**
     * 根据 认证凭证[authentication] 获取用户名
     */
    private fun extractPrincipal(authentication: Authentication?): String? {
        if(authentication == null) {
            return null
        }
        return when (authentication.principal) {
            is UserDetails -> {
                val springSecurityUser = authentication.principal as UserDetails
                springSecurityUser.username
            }
            is String -> authentication.principal as String
            else -> null
        }
    }
}