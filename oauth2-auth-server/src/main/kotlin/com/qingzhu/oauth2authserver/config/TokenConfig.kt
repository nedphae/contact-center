package com.qingzhu.oauth2authserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec


@Configuration
class TokenConfig {

    /**
     * token的持久化
     *
     * @return JwtTokenStore
     */
    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    /**
     * jwt token的生成配置
     *
     * @return
     */
    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(keyPair())
        return converter
    }

    @Bean
    fun keyPair(): KeyPair {
        return try {
            val privateExponent =
                "3851612021791312596791631935569878540203393691253311342052463788814433805390794604753109719790052408607029530149004451377846406736413270923596916756321977922303381344613407820854322190592787335193581632323728135479679928871596911841005827348430783250026013354350760878678723915119966019947072651782000702927096735228356171563532131162414366310012554312756036441054404004920678199077822575051043273088621405687950081861819700809912238863867947415641838115425624808671834312114785499017269379478439158796130804789241476050832773822038351367878951389438751088021113551495469440016698505614123035099067172660197922333993"
            val modulus =
                "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683"
            val exponent = "65537"
            val publicSpec = RSAPublicKeySpec(BigInteger(modulus), BigInteger(exponent))
            val privateSpec = RSAPrivateKeySpec(BigInteger(modulus), BigInteger(privateExponent))
            val factory: KeyFactory = KeyFactory.getInstance("RSA")
            KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec))
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}