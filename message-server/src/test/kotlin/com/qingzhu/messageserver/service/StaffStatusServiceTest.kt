package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.dto.StaffStatusDto
import com.qingzhu.messageserver.domain.entity.StaffStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono


class StaffStatusServiceTest : MessageServerApplicationTests() {
    @Autowired
    private lateinit var staffStatusService: StaffStatusService

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    @Test
    fun testLPush() {
        val listOps: ReactiveListOperations<String, String> = redisTemplate.opsForList()
        Mono.just(9491)
            .flatMap { listOps.delete("queue:9491") }
            .flatMap { listOps.leftPush("queue:9491", it.toString()) }
            .map { println(it) }
            .flatMap { listOps.leftPush("queue:9491", it.toString()) }
            .map { println(it) }
            .flatMap { listOps.rightPop("queue:9491") }
            .map { println(it) }
            .flatMap { listOps.size("queue:9491") }
            .subscribe { println(it) }
        runBlocking {
            delay(5000L)
        }
    }

    @Test
    fun testStaffStatus() {
        staffStatusService.saveStatus(
            StaffStatus(
                1, 2L, StaffAuthority.ROLE_STAFF, listOf(3L, 4L), 1,
                mapOf(3L to 15), 10, 1
            )
        )
        staffStatusService.saveStatus(
            StaffStatusDto(
                1, 12L, StaffAuthority.ROLE_STAFF, listOf(13L, 4L), 1,
                mapOf(4L to 15), maxServiceCount = 10, staffType = 1
            ).toStaffStatus()
        )

        val staffStatusList = staffStatusService.findIdleStaff(1, 4L)
        println(staffStatusList)

        assertFalse(staffStatusList.isEmpty())

        staffStatusService.setStatusOffline(StaffChangeStatusDto(1, 2L, null))
        val result = staffStatusService.findIdleStaff(1, 4L)
        println(result)
        assertEquals(1, result.size)
    }
}