package com.qingzhu.common.security

import org.apache.commons.logging.LogFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.core.log.LogMessage
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.StringUtils

class RoleBaseJwtGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    private val logger = LogFactory.getLog(javaClass)

    companion object {
        private const val DEFAULT_AUTHORITY_PREFIX = "SCOPE_"
        private val WELL_KNOWN_AUTHORITIES_CLAIM_NAMES: Collection<String> = listOf("scope", "scp")

        private const val ROLE_AUTHORITY_PREFIX = "ROLE_"
        private val ROLE_WELL_KNOWN_AUTHORITIES_CLAIM_NAMES: Collection<String> = listOf("authorities")
    }

    /**
     * Extract [GrantedAuthority]s from the given [Jwt].
     * @param jwt The [Jwt] token
     * @return The [authorities][GrantedAuthority] read from the token scopes
     */
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val grantedAuthorities: MutableCollection<GrantedAuthority> = ArrayList()
        for (authority in getAuthorities(jwt)) {
            grantedAuthorities.add(SimpleGrantedAuthority(DEFAULT_AUTHORITY_PREFIX + authority))
        }
        for (authority in getRoleAuthorities(jwt)) {
            val authorityBuilder = StringBuilder()
            if (!authority.startsWith(ROLE_AUTHORITY_PREFIX)) {
                authorityBuilder.append(ROLE_AUTHORITY_PREFIX)
            }
            authorityBuilder.append(authority)
            grantedAuthorities.add(SimpleGrantedAuthority(authorityBuilder.toString()))
        }
        return grantedAuthorities
    }

    private fun getRoleAuthorities(jwt: Jwt): Collection<String> {
        val claimName = getRoleAuthoritiesClaimName(jwt)
        return getAuthorities(jwt, claimName)
    }

    private fun getRoleAuthoritiesClaimName(jwt: Jwt): String? {
        for (claimName in ROLE_WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.containsClaim(claimName)) {
                return claimName
            }
        }
        return null
    }

    private fun getAuthoritiesClaimName(jwt: Jwt): String? {
        for (claimName in WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.containsClaim(claimName)) {
                return claimName
            }
        }
        return null
    }

    private fun getAuthorities(jwt: Jwt): Collection<String> {
        val claimName = getAuthoritiesClaimName(jwt)
        return getAuthorities(jwt, claimName)
    }

    private fun getAuthorities(jwt: Jwt, claimName: String?): Collection<String> {
        if (claimName == null) {
            logger.trace("Returning no authorities since could not find any claims that might contain scopes")
            return emptyList()
        }
        if (logger.isTraceEnabled) {
            logger.trace(LogMessage.format("Looking for scopes in claim %s", claimName))
        }
        val authorities = jwt.getClaim<Any>(claimName)
        if (authorities is String) {
            return if (StringUtils.hasText(authorities)) {
                listOf(*authorities.split(" ").toTypedArray())
            } else emptyList()
        }
        return if (authorities is Collection<*>) {
            @Suppress("UNCHECKED_CAST")
            authorities as Collection<String>
        } else emptyList()
    }
}