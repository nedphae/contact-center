package com.qingzhu.common.security.password

import com.qingzhu.common.security.password.PasswordEncoder.bCryptPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoder {
    @JvmStatic
    val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
}

fun getBCryptPasswordEncoder() = bCryptPasswordEncoder