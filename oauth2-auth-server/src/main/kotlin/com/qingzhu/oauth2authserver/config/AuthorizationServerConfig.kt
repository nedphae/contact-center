package com.qingzhu.oauth2authserver.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.qingzhu.oauth2authserver.security.StaffTokenEnhancer
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.approval.ApprovalStore
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    private val accessTokenValiditySeconds = 3600 //资源令牌验证过期时间

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var accessTokenConverter: JwtAccessTokenConverter

    @Autowired
    private lateinit var staffTokenEnhancer: StaffTokenEnhancer

    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        // 支持将client参数放在header或body中
        oauthServer.allowFormAuthenticationForClients()
        oauthServer.tokenKeyAccess("isAuthenticated()")
            .checkTokenAccess("permitAll()")
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        // 配置客户端信息，从数据库中读取，对应oauth_client_details表
        clients.inMemory()
            // 外部权限
            .withClient("user_client")
            // secret 必须为加密好的字符串 "test_secret"
            .secret("\$2a\$10\$lPaP9cSdF6QugTCJV4ntAuY1IML7V228WoPxdfDn.BHJqzbvLXoqu")
            .authorizedGrantTypes("authorization_code", "implicit", "password", "refresh_token")
            .authorities("ROLE_ADMIN")
            // scope 可以分组， eg im:read， 但是验证时需要整个字符匹配
            .scopes("oauth2", "bot", "staff", "im", "dispatcher", "msg")
            .authorities()
            // .resourceIds("oauth2", "bot", "cs-admin", "im")
            .accessTokenValiditySeconds(accessTokenValiditySeconds)
            .and()
            // 内部权限
            .withClient("inner_client")
            // secret 必须为加密好的字符串
            .secret("\$2a\$10\$lPaP9cSdF6QugTCJV4ntAuY1IML7V228WoPxdfDn.BHJqzbvLXoqu")
            .authorizedGrantTypes("client_credentials") // 客户端模式需要单独设置
            .authorities("ROLE_ADMIN")
            .scopes("oauth2", "bot", "staff", "im", "dispatcher", "msg")
            .authorities()
            // .resourceIds("oauth2", "bot", "cs-admin")
            .accessTokenValiditySeconds(accessTokenValiditySeconds)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        // 配置token的数据源、自定义的tokenServices等信息,配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
        endpoints.tokenStore(tokenStore)
            .authorizationCodeServices(authorizationCodeServices())
            .approvalStore(approvalStore())
            .tokenEnhancer(tokenEnhancerChain())
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService)
    }

    @Bean
    fun approvalStore(): ApprovalStore {
        return InMemoryApprovalStore()
    }

    /**
     * 自定义token
     *
     * @return tokenEnhancerChain
     */
    @Bean
    fun tokenEnhancerChain(): TokenEnhancerChain? {
        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(listOf(staffTokenEnhancer, accessTokenConverter))
        return tokenEnhancerChain
    }

    /**
     * 授权码模式持久化授权码code
     *
     * @return JdbcAuthorizationCodeServices
     */
    @Bean
    protected fun authorizationCodeServices(): AuthorizationCodeServices {
        // 授权码存储等处理方式类，使用jdbc，操作oauth_code表
        return InMemoryAuthorizationCodeServices()
    }

    @FrameworkEndpoint
    internal class JwkSetEndpoint(var keyPair: KeyPair) {
        @ResponseBody
        @GetMapping("/.well-known/jwks.json")
        fun getKey(): JSONObject {
            val publicKey = this.keyPair.public
            val key = RSAKey.Builder(publicKey as RSAPublicKey).build()
            return JWKSet(key).toJSONObject()
        }

    }

}