package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.dto.ReceptionistShuntDto
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.query.StaffQuery
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import com.qingzhu.staffadmin.staff.service.StaffService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/staff")
class StaffController(
        private val staffRepository: ReactiveStaffRepository,
        private val staffService: StaffService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun registerCustomerService(@RequestBody @Valid staffQuery: StaffQuery): Mono<Staff> {
        return staffRepository.save(staffQuery.toCustomerServiceRepresentative())
    }

    @GetMapping
    fun findFirstByUsername(organizationId: Int, username: String?): Mono<InnerUser> {
        return staffService.findFirstByOrganizationIdAndUsername(organizationId, username)
    }
}

@RestController
class StaffHandler(private val staffService: StaffService) {
    suspend fun findStaffInfo(sr: ServerRequest): ServerResponse {
        return sr.principal()
                .getPrincipalTriple()
                .flatMap {(_, sid, _) ->
                    sid.flatMap { staffService.findStaffInfo(it) }
                }
                .transform {
                    ok().body(it)
                }
                .awaitSingle()
    }

    suspend fun findStaffConfigByOrganizationIdAndStaffId(sr: ServerRequest): ServerResponse {
        return sr.queryParam("organizationId").map(String::toInt).map { oi ->
            ok().contentType(MediaType.APPLICATION_JSON)
                    .body<ReceptionistShuntDto>(sr.queryParam("staffId").map(String::toLong).map { si ->
                        staffService.findStaffConfigByOrganizationIdAndStaffId(oi, si)
                    }.orElseGet {
                        staffService.findStaffConfigByOrganizationIdAndStaffId(oi)
                    })
        }.orElseGet {
            ok().build()
        }.awaitSingle()
    }
}
