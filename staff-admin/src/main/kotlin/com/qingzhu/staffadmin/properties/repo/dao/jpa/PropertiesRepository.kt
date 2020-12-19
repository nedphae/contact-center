package com.qingzhu.staffadmin.properties.repo.dao.jpa

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PropertiesRepository : JpaRepository<Properties, Long> {
    fun findDistinctTopByKey(key: String): Optional<Properties>
}