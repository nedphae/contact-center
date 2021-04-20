package com.qingzhu.imaccess.config

import com.qingzhu.common.security.oauth2ResourceServerConfig
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/oss/**").permitAll()
                .pathMatchers("/websocket-address/**").hasAuthority("SCOPE_im")
                .anyExchange().authenticated()
                .and()
                .oauth2Client()
                .and()
                .oauth2ResourceServerConfig()
        return http.build()
    }
}