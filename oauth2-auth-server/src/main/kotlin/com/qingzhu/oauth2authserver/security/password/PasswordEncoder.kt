package com.qingzhu.oauth2authserver.security.password

import com.qingzhu.oauth2authserver.security.password.PasswordEncoder.bCryptPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoder {
    @JvmStatic
    val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
}

fun getBCryptPasswordEncoder() = bCryptPasswordEncoder