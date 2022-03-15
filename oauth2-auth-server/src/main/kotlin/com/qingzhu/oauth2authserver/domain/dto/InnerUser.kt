package com.qingzhu.oauth2authserver.domain.dto

import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User


data class InnerUser(
    val organizationId: Int,
    val id: Long,
    val username: String,
    val password: String,
    var role: String
) {
    fun toMyUser(): MyUser {
        return MyUser(organizationId, id, username, password, role)
    }
}

class MyUser(
    val organizationId: Int,
    val id: Long,
    username: String,
    password: String,
    role: String
) : User(username, password, AuthorityUtils.createAuthorityList(role))