package com.qingzhu.staffadmin.config

import com.qingzhu.common.security.oauth2ResourceServerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/staff").hasAuthority("SCOPE_staff")
            // .pathMatchers("/staff/info").hasRole("ADMIN")
            .anyExchange().authenticated()
            .and()
            .oauth2Client()
            .and()
            .oauth2ResourceServerConfig()
        return http.build()
    }
}

