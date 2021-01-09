package com.qingzhu.bot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity, keyPair: KeyPair): SecurityWebFilterChain {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                // /* 无效 必须 /**
                .pathMatchers("/**").hasAuthority("SCOPE_bot")
                .anyExchange().authenticated()
                .and()
                .oauth2Client()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .publicKey(keyPair.public as RSAPublicKey)
        return http.build()
    }

}

