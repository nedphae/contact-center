package com.qingzhu.common.security

import com.qingzhu.common.domain.AbstractAuditingEntity
import com.qingzhu.common.domain.AbstractStaffEntity
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.BearerTokenErrors
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.function.Tuple3
import java.math.BigInteger
import java.security.KeyFactory
import java.security.Principal
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*
import java.util.regex.Pattern

val jwtReactiveAuthenticationManager = JwtReactiveAuthenticationManager(reactiveJwtDecoder())

/**
 * Utility class for Spring WebFlux Security.
 */
object SecurityUtils {
    private val authorizationPattern = Pattern.compile(
        "^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
        Pattern.CASE_INSENSITIVE
    )

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
            // 内部定时器调用时没有用户 所以默认 sys
            .switchIfEmpty(Mono.just("sys"))
    }

    /**
     * 根据 认证凭证[authentication] 获取用户名
     */
    private fun extractPrincipal(authentication: Authentication?): String? {
        if (authentication == null) {
            return null
        }
        return when (authentication.principal) {
            is UserDetails -> {
                val springSecurityUser = authentication.principal as UserDetails
                springSecurityUser.username
            }
            is Jwt -> authentication.name
            is String -> authentication.principal as String
            else -> null
        }
    }

    fun getBearerAuthentication(headers: HttpHeaders): Authentication? {
        val authorization = headers.getFirst(HttpHeaders.AUTHORIZATION)
        return getBearerAuthentication(authorization)
    }

    fun getBearerAuthentication(authorization: String?): Authentication? {
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null
        }
        val matcher = authorizationPattern.matcher(authorization!!)
        if (!matcher.matches()) {
            val error = BearerTokenErrors.invalidToken("Bearer token is malformed")
            throw OAuth2AuthenticationException(error)
        }
        val token = matcher.group("token")
        if (token.isEmpty()) {
            val error = BearerTokenErrors.invalidToken("Bearer token is malformed")
            throw OAuth2AuthenticationException(error)
        }
        return BearerTokenAuthenticationToken(token)
    }
}

fun ServerHttpSecurity.oauth2ResourceServerConfig() {
    this.oauth2ResourceServer()
        .jwt {
            it.jwtDecoder(reactiveJwtDecoder())
            val converter = RoleBaseJwtGrantedAuthoritiesConverter()
            val c = JwtAuthenticationConverter()
            c.setJwtGrantedAuthoritiesConverter(converter)
            it.jwtAuthenticationConverter(ReactiveJwtAuthenticationConverterAdapter(c))
        }
}

/**
 * 使用 oauth2Client 获取 jwt 时没有 principalName， 所以这里要手动设置
 *
 * See [principalname-cannot-be-empty-error](https://stackoverflow.com/questions/63352692/spring-security-5-with-oauth2-causing-principalname-cannot-be-empty-error)
 */
fun reactiveJwtDecoder(): ReactiveJwtDecoder {
    val delegatingOAuth2TokenValidator = DelegatingOAuth2TokenValidator(JwtTimestampValidator())
    val delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap())
    return NimbusReactiveJwtDecoder(getPublicKey() as RSAPublicKey)
        .also { decoder ->
            decoder.setJwtValidator(delegatingOAuth2TokenValidator)
            decoder.setClaimSetConverter {
                val convertedClaims = delegate.convert(it)
                val username = convertedClaims?.get("user_name") ?: "sys"
                convertedClaims!!["sub"] = username
                convertedClaims
            }
        }
}

fun getPublicKey(): PublicKey {
    val modulus =
        "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683"
    val exponent = "65537"
    val publicSpec = RSAPublicKeySpec(BigInteger(modulus), BigInteger(exponent))
    val factory: KeyFactory = KeyFactory.getInstance("RSA")
    return factory.generatePublic(publicSpec)
}

/**
 * 获取用户 [Principal] 并解析为 [Triple]，包括 机构id，客服id，客服名称
 */
fun Mono<out Principal>.getPrincipalTriple(): Mono<Tuple3<Int, Long, String>> {
    return this.flatMap {
        val principal = getPrincipalTriple(it)
        Mono.zip(
            Mono.justOrEmpty(principal.first),
            Mono.justOrEmpty(principal.second),
            Mono.justOrEmpty(principal.third)
        )
    }
}

inline fun <reified T : AbstractStaffEntity> ServerRequest.bodyToMonoWithOrgAndStaff(): Mono<T> {
    return this.bodyToMono<T>()
        .flatMap { this.principal().setOrganizationIdAndStaffId(it) }
}

inline fun <reified T : AbstractAuditingEntity> ServerRequest.bodyToMonoWithOrg(): Mono<T> {
    return this.bodyToMono<T>()
        .flatMap { this.principal().setOrganizationId(it) }
}

fun <T : AbstractAuditingEntity> Mono<out Principal>.setOrganizationId(entity: T): Mono<T> {
    return this
        .getPrincipalTriple()
        .map {
            entity.organizationId = it.t1
            entity
        }
}

fun <T : AbstractStaffEntity> Mono<out Principal>.setOrganizationIdAndStaffId(entity: T): Mono<T> {
    return this
        .getPrincipalTriple()
        .map {
            entity.organizationId = it.t1
            entity.staffId = it.t2
            entity
        }
}

fun getPrincipalTriple(principal: Principal): Triple<Int?, Long?, String?> {
    val jwtPrincipal = (principal as JwtAuthenticationToken).principal as? Jwt
    // org id
    val orgId = jwtPrincipal?.getClaim<Long>("oid")?.toInt()
    // staff id
    val sid = jwtPrincipal?.getClaim<Long>("sid")
    // staff name
    val username = principal.name
    return Triple(orgId, sid, username)
}

/**
 * 协程 获取用户 [Principal] 并解析为 [Triple]，包括 机构id，客服id，客服名称
 */
suspend fun ServerRequest.awaitPrincipalTriple(): Triple<Int?, Long?, String?> {
    val principal = this.awaitPrincipal()
    return getPrincipalTriple(principal!!)
}

suspend inline fun <reified T : AbstractAuditingEntity> ServerRequest.awaitPrincipalTripleWithBodyOrg(): T? {
    val entity = this.awaitBodyOrNull<T>()
    val (orgId, _, _) = this.awaitPrincipalTriple()
    entity?.organizationId = orgId
    return entity
}

suspend inline fun <reified T : AbstractStaffEntity> ServerRequest.awaitPrincipalTripleWithBodyOrgAndStaff(): T {
    val entity = this.awaitBody<T>()
    val (orgId, staffId, _) = this.awaitPrincipalTriple()
    entity.organizationId = orgId
    entity.staffId = staffId
    return entity
}

private fun getContextWith(oldContext: Context, authentication: Authentication?): Context {
    var context = oldContext
    if (authentication != null) {
        val securityContext = SecurityContextImpl()
        val securityContextMono = jwtReactiveAuthenticationManager.authenticate(authentication)
            .map {
                securityContext.authentication = it
                securityContext
            }.checkpoint("securityContext mono")
        // fuck fuck fuck context 要返回 put 的结果，操，忘了
        context = oldContext.put(SecurityContext::class.java, securityContextMono)
    }
    return context
}

fun <T> Mono<T>.withAuthentication(authentication: Authentication?): Mono<T> {
    return this
        .checkpoint("webflux mono")
        .contextWrite { getContextWith(it, authentication) }
}

fun <T> Flux<T>.withAuthentication(authentication: Authentication?): Flux<T> {
    return this
        .checkpoint("webflux flux")
        .contextWrite { getContextWith(it, authentication) }
}

suspend fun <T> Mono<T>.awaitWithAuthentication(authentication: Authentication?): T? {
    return this
        .withAuthentication(authentication)
        .awaitSingleOrNull()
}

suspend fun <T : Any> Flux<T>.awaitWithAuthentication(authentication: Authentication?): List<T> {
    return this
        .withAuthentication(authentication)
        .asFlow().toList()
}