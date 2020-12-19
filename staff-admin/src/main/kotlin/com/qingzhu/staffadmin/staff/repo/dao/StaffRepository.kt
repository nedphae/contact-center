package com.qingzhu.staffadmin.staff.repo.dao

import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StaffRepository : JpaRepository<Staff, Long>  {

    fun findFirstByOrganizationIdAndUsername(organizationId: Int, username: String): Optional<Staff>

}