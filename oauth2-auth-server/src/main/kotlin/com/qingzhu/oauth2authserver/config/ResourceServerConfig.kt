package com.qingzhu.oauth2authserver.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore


@Configuration
@EnableResourceServer
@EnableOAuth2Client
class ResourceServerConfig : ResourceServerConfigurerAdapter() {
    @Autowired
    private lateinit var tokenStore: TokenStore

    override fun configure(resourceServerSecurityConfigurer: ResourceServerSecurityConfigurer) {
        resourceServerSecurityConfigurer
            .tokenStore(tokenStore)
            .resourceId("oauth2")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/users/*")
            .access("#oauth2.hasScope('oauth2')")

    }
}