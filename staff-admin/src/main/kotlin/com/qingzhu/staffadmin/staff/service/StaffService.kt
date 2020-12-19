package com.qingzhu.staffadmin.staff.service

import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistGroupDto
import com.qingzhu.staffadmin.staff.domain.dto.StaffDto
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.repo.dao.StaffConfigRepository
import com.qingzhu.staffadmin.staff.repo.dao.StaffRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.*


@Service
class StaffService(
        private val staffRepository: StaffRepository,
        private val staffConfigRepository: StaffConfigRepository
) {

    fun findFirstByOrganizationIdAndUsername(organizationId: Int, username: String?): InnerUser {
        return Optional.ofNullable(username).flatMap { staffRepository.findFirstByOrganizationIdAndUsername(organizationId, it) }.map {
            InnerUser(it.organizationId, it.id ?: -1, it.username, it.password, it.role.name)
        }.orElseThrow { UsernameNotFoundException("用户[$username]不存在") }
    }

    fun findStaffInfo(staffId: Long): Mono<StaffDto> {
        return Mono.justOrEmpty(staffRepository.findById(staffId))
                .map { StaffDto.fromStaff(it) }
    }

    fun findStaffConfigByOrganizationIdAndStaffId(organizationId: Int, staffId: Long? = null): Mono<ReceptionistGroupDto> {
        var staff: Optional<Staff> = Optional.empty()
        val staffConfigFlux = if (staffId == null) {
            staffConfigRepository.findAllByOrganizationId(organizationId).toFlux()
        } else {
            staff = staffRepository.findById(staffId)
            staffConfigRepository.findAllByOrganizationIdAndStaffId(organizationId, staffId).toFlux()
        }

        return staffConfigFlux.collectMap({ it.shuntId }) { it.priority }.map {
            ReceptionistGroupDto(
                    organizationId,
                    staffId,
                    it.keys.toList(),
                    it,
                    staff.map(Staff::simultaneousService).orElse(0)
            )
        }
    }
}