package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import reactor.core.publisher.Mono

interface AssignmentInterface {
    fun assignmentStaff(list: List<StaffDispatcherDto>): Mono<Long>
}