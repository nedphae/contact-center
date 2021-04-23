package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.ShuntUIConfig
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ShuntUIConfigRepository : CoroutineSortingRepository<ShuntUIConfig, Long> {
    suspend fun findByOrganizationIdAndShuntId(organizationId: Int, shuntId: Long): ShuntUIConfig?

    fun findByOrganizationId(organizationId: Int): Flow<ShuntUIConfig>
}