package com.qingzhu.oauth2authserver.security

import com.google.common.collect.Maps
import com.qingzhu.oauth2authserver.domain.dto.MyUser
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.stereotype.Component


@Component
class StaffTokenEnhancer : TokenEnhancer {

    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        val additionalInfo = Maps.newHashMap<String, Any>()
        if (authentication.principal is User) {
            additionalInfo["oid"] = (authentication.principal as MyUser).organizationId
            additionalInfo["sid"] = (authentication.principal as MyUser).id
            (accessToken as DefaultOAuth2AccessToken).additionalInformation = additionalInfo
        }
        return accessToken
    }
}