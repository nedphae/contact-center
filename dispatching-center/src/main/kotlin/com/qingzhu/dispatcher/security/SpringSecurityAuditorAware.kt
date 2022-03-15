package com.qingzhu.dispatcher.security

import com.qingzhu.common.security.SecurityUtils.getCurrentUserLogin
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SpringSecurityAuditorAware : ReactiveAuditorAware<String> {
    override fun getCurrentAuditor(): Mono<String> {
        return getCurrentUserLogin()
    }
}
