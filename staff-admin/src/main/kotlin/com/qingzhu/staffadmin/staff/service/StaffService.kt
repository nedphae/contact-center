package com.qingzhu.staffadmin.staff.service

import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistShuntDto
import com.qingzhu.staffadmin.staff.domain.dto.StaffDto
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
    fun findStaffInfo(staffId: Long): Mono<StaffDto> {
        return staffRepository.findById(staffId)
                .map { StaffDto.fromStaff(it) }
    }

    fun findStaffConfigByOrganizationIdAndStaffId(organizationId: Int, staffId: Long? = null): Mono<ReceptionistShuntDto> {
        var staff: Mono<Staff> = Mono.empty()
        val staffConfigFlux = if (staffId == null) {
            staffConfigRepository.findAllByOrganizationId(organizationId)
        } else {
            staff = staffRepository.findById(staffId)
            staffConfigRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId)
        }

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