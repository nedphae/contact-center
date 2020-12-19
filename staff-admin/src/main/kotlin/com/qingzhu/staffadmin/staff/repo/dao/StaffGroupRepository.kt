package com.qingzhu.staffadmin.staff.repo.dao

import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface  StaffGroupRepository : JpaRepository<StaffGroup, Long> {

    fun findDistinctTopByGroupName(groupName: String): Optional<StaffGroup>
}