package com.qingzhu.messageserver.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt { jwt ->
                    jwt.jwtDecoder(NimbusReactiveJwtDecoder(getPublicKey() as RSAPublicKey).also { decoder ->
                        decoder.setJwtValidator {
                            // 设置资源 id
                            if (it.audience.contains("im")) {
                                OAuth2TokenValidatorResult.success()
                            } else {
                                OAuth2TokenValidatorResult.failure()
                            }
                        }
                    })
                }
        return http.build()
    }

    fun getPublicKey(): PublicKey {
        val modulus = "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683"
        val exponent = "65537"
        val publicSpec = RSAPublicKeySpec(BigInteger(modulus), BigInteger(exponent))
        val factory: KeyFactory = KeyFactory.getInstance("RSA")
        return factory.generatePublic(publicSpec)
    }
}