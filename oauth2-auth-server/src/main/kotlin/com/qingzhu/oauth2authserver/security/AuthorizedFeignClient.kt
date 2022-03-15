package com.qingzhu.oauth2authserver.security

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.core.annotation.AliasFor
import kotlin.reflect.KClass


/**
 * 服务间 内部调用 FeignClient
 * 自动授权
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@MustBeDocumented
@FeignClient
annotation class AuthorizedFeignClient(
    @get:AliasFor(annotation = FeignClient::class, attribute = "name")
    val name: String = "",
    @get:AliasFor(annotation = FeignClient::class, attribute = "configuration")
    val configuration: Array<KClass<*>> = [OAuth2InterceptedFeignConfiguration::class],
    val url: String = "",
    val decode404: Boolean = false,
    val fallback: KClass<*> = Void::class,
    val path: String = ""
)