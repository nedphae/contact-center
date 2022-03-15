package com.qingzhu.staffadmin.staff.controller

import com.qingzhu.common.security.getPrincipalTriple
import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.staff.domain.dto.InnerUser
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.StaffConfig
import com.qingzhu.staffadmin.staff.domain.query.StaffQuery
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import com.qingzhu.staffadmin.staff.service.StaffService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/staff")
class StaffController(
    private val staffRepository: ReactiveStaffRepository,
    private val staffService: StaffService,
    private val reactorRedisCache: ReactorRedisCache,
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun registerCustomerService(principal: Principal, @RequestBody @Valid staffQuery: StaffQuery): Mono<Staff> {
        staffQuery.organizationId = getPrincipalTriple(principal).first
        val mapStaff = DtoMapper.mapper.mapStaffQueryToStaff(staffQuery)
        val password = if (staffQuery.id != null && staffQuery.password.isNullOrBlank()) {
            // 空就不更新密码
            staffService.findStaffInfo(staffQuery.id).flatMap { Mono.justOrEmpty(it.password) }
        } else Mono.justOrEmpty(getBCryptPasswordEncoder().encode(staffQuery.password))
        return password
            .map { mapStaff.also { staff -> staff.password = it } }
            .switchIfEmpty(Mono.just(mapStaff))
            .flatMap(staffRepository::save)
            .flatMap { staff -> reactorRedisCache.removeKey("staff:${staff.organizationId}").map { staff } }
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

    suspend fun deleteStaffByIds(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        val ids = sr.bodyToFlux<Long>()
        return staffService.deleteStaffByIds(organizationId!!, ids)
            .then(ok().build()).awaitSingle()
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

    suspend fun findStaffConfigByShuntId(sr: ServerRequest): ServerResponse {
        val shuntId = sr.pathVariable("id")
        return ok().bodyAndAwait(
            sr.principal()
                .getPrincipalTriple()
                .flatMapMany { staffService.findStaffConfigByShuntId(it.t1, shuntId.toLong()) }
                .asFlow()
        )
    }

    suspend fun saveStaffConfig(sr: ServerRequest): ServerResponse {
        val (organizationId) = sr.awaitGetOrganizationId()
        val body = sr.bodyToFlux<StaffConfig>()
            .doOnNext {
                it.organizationId = organizationId
            }
            .transform { staffService.saveStaffConfig(it, organizationId!!) }
        return ok().body(body).awaitSingle()
    }


}
