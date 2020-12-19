package com.qingzhu.dispatcher.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey


/**
 * EnableReactiveMethodSecurity 推荐注解到 service 层
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity, keyPair: KeyPair): SecurityWebFilterChain {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/customer/*").permitAll()
                .pathMatchers("/*").hasAuthority("SCOPE_dispatcher")
                .anyExchange().authenticated()
                .and()
                // .oauth2Client()
                // .and()
                .oauth2ResourceServer()
                .jwt()
                .publicKey(keyPair.public as RSAPublicKey)
        return http.build()
    }

}