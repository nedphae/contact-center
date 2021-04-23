package com.qingzhu.staffadmin.staff.service

import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistShuntDto
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffConfigRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StaffService(
        private val staffRepository: ReactiveStaffRepository,
        private val staffConfigRepository: ReactiveStaffConfigRepository
) {

    fun findFirstByOrganizationIdAndUsername(organizationId: Int, username: String?): Mono<InnerUser> {
        return Mono.justOrEmpty(username).flatMap { staffRepository.findFirstByOrganizationIdAndUsername(organizationId, it) }.map {
            InnerUser(it.organizationId, it.id!!, it.username, it.password, it.role.name)
        }.switchIfEmpty(Mono.error(UsernameNotFoundException("用户[$username]不存在")))
    }

    /**
     * 当前 spring 版本不支持直接获取 ROLE，需要自定义 AuthenticationConverter
     */
    @PreAuthorize("hasRole('ADMIN')")
    fun findStaffInfo(staffId: Long): Mono<Staff> {
        return staffRepository.findById(staffId)
    }

    fun findStaffConfigByOrganizationIdAndStaffId(organizationId: Int, staffId: Long): Mono<ReceptionistShuntDto> {
        val staff: Mono<Staff> = staffRepository.findById(staffId)
        val staffConfigFlux = staffConfigRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId)

        return staffConfigFlux
                .collectMap({ it.shuntId }) { it.priority }
                .transform {
                    Mono.zip(staff, it)
                }.map {
                    ReceptionistShuntDto(
                            organizationId,
                            staffId,
                            it.t2.keys.toList(),
                            it.t2,
                            it.t1.simultaneousService)
                }
    }
}