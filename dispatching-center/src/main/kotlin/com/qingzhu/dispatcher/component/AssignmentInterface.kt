package com.qingzhu.dispatcher.component

import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AssignmentInterface {
    fun assignmentStaff(flux: Flux<StaffDispatcherDto>): Mono<Long>
}