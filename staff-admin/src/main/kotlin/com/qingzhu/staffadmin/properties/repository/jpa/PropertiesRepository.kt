package com.qingzhu.staffadmin.properties.repository.jpa

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.springframework.data.jpa.repository.JpaRepository

interface PropertiesRepository : JpaRepository<Properties, Long>