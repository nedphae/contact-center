package com.qingzhu.dispatcher.service

import reactor.core.publisher.Mono

interface AssignmentInterface {
    fun assignmentStaff(organizationId: Int, shuntId: Long): Mono<Long>
}