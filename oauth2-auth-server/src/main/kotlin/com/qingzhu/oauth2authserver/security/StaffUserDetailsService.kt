package com.qingzhu.oauth2authserver.security

import com.qingzhu.oauth2authserver.service.StaffService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


/**
 * 实现多租户还是使用 client id
 * 这里获取到 client id 做机构 ID
 */
@Service("userDetailsService")
class StaffUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var staffService: StaffService

    override fun loadUserByUsername(username: String?): User {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val orgId = request.getParameter("org_id")
        val innerUser = staffService.findFirstByUsername(orgId.toInt(), username)
        return innerUser?.toMyUser() ?: throw UsernameNotFoundException("$username not found")
    }


}