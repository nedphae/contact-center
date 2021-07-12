package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.query.StaffQuery
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import com.qingzhu.staffadmin.staff.service.StaffService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyAndAwait
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/staff")
class StaffController(
    private val staffRepository: ReactiveStaffRepository,
    private val staffService: StaffService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun registerCustomerService(principal: Principal, @RequestBody @Valid staffQuery: StaffQuery): Mono<Staff> {
        staffQuery.organizationId = getPrincipalTriple(principal).first
        return staffRepository.save(DtoMapper.mapper.mapStaffQueryToStaff(staffQuery))
    }

    @GetMapping
    fun findFirstByUsername(organizationId: Int, username: String?): Mono<InnerUser> {
        return staffService.findFirstByOrganizationIdAndUsername(organizationId, username)
    }
}

@RestController
class StaffHandler(private val staffService: StaffService) {
    suspend fun findStaffInfo(sr: ServerRequest): ServerResponse {
        val (_, staffId) = sr.awaitGetOrganizationId()
        val staffInfo = if (staffId != null) {
            staffService.findStaffInfo(staffId)
        } else Mono.empty()

        return staffInfo
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun findStaffConfigByOrganizationIdAndStaffId(sr: ServerRequest): ServerResponse {
        val (organizationId, staffId) = sr.awaitGetOrganizationId()
        return if (organizationId != null && staffId != null) {
            ok().contentType(MediaType.APPLICATION_JSON)
                .body(staffService.findStaffConfigByOrganizationIdAndStaffId(organizationId, staffId))
        } else {
            ok().build()
        }.awaitSingle()
    }

    suspend fun findAllEnabledBotStaff(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(staffService.findAllEnabledBotStaff().asFlow())
    }

    suspend fun findAllStaff(sr: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { staffService.findAllStaff(it.t1) }
                .asFlow()
        )
    }
}
