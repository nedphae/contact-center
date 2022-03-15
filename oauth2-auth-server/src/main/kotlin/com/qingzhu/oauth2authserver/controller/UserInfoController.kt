package com.qingzhu.oauth2authserver.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
class UserInfoController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/oauth/users/me")
    fun user(principal: Principal): Principal {
        return principal
    }
}