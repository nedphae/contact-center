package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistShuntDto
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
        staffQuery.organizationId = (principal as Jwt).getClaim<Long>("oid").toInt()
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
        val oid = sr.queryParam("organizationId").orElse(null)
        val sid = sr.queryParam("staffId").orElse(null)
        val staffInfo = if (sid != null) {
            staffService.findStaffInfo(sid.toLong())
        } else {
            sr.principal()
                .getPrincipalTriple()
                .flatMap { staffService.findStaffInfo(it.t2) }
        }

        return staffInfo
            .transform { ok().body(it) }
            .awaitSingle()
    }

    suspend fun findStaffConfigByOrganizationIdAndStaffId(sr: ServerRequest): ServerResponse {
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            sr.queryParam("staffId").map(String::toLong).map { si ->
                ok().contentType(MediaType.APPLICATION_JSON)
                    .body<ReceptionistShuntDto>(staffService.findStaffConfigByOrganizationIdAndStaffId(oi, si))
            }.orElseGet { ok().build() }
        }.orElseGet { ok().build() }.awaitSingle()
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
