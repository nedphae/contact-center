package com.qingzhu.staffadmin.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore


// @EnableResourceServer
// @EnableOAuth2Client
class OldResourceServerConfig : ResourceServerConfigurerAdapter() {
    @Autowired
    private lateinit var tokenStore: TokenStore

    override fun configure(resourceServerSecurityConfigurer: ResourceServerSecurityConfigurer) {
        resourceServerSecurityConfigurer
                .tokenStore(tokenStore)
                .resourceId("staff-admin")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/staff/*").permitAll()
                .antMatchers("/*")
                .access("#oauth2.hasScope('test')")
    }

    //
}

