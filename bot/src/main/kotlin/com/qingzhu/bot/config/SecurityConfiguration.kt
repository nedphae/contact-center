package com.qingzhu.bot.config

import com.qingzhu.common.security.oauth2ResourceServerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("GET")
        source.registerCorsConfiguration("/bot/qa", config)

        http.cors().configurationSource(source)

        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/bot/qa").permitAll()
            .pathMatchers("/users/**").permitAll()
            // /* 无效 必须 /**
            .pathMatchers("/**").hasAuthority("SCOPE_bot")
            .anyExchange().authenticated()
            // oauth2Client 与 oauth2ResourceServer 冲突
            .and()
            .oauth2Client()
            .and()
            .oauth2ResourceServerConfig()
        return http.build()
    }

}

