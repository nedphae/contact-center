package com.qingzhu.staffadmin.staff.repository

import com.qingzhu.staffadmin.staff.domain.entity.ShuntClass
import org.springframework.data.repository.reactive.ReactiveSortingRepository

interface ReactiveShuntClassRepository : ReactiveSortingRepository<ShuntClass, Long> {
}