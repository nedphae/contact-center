package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.dto.StaffStatusDto
import com.qingzhu.messageserver.domain.entity.StaffStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class StaffStatusServiceTestWithoutSpring {

    @Test
    fun testCopy() {
        val staffStatus = StaffStatus(
            1, 1L, StaffAuthority.ROLE_STAFF, setOf(3L, 4L), 1,
             10, 1
        )
        staffStatus.priorityOfShuntMap = mapOf(3L to 15)
        staffStatus.userIdList.add(1)
        staffStatus.currentServiceCount = 20
        val copyOf = staffStatus.copy()
        assertEquals(staffStatus, copyOf)
    }
}

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
    fun testReplace() {
        val oldStatus = StaffStatus(
            9491, 2, StaffAuthority.ROLE_STAFF, setOf(1L), 0, 9999, 0
        )
        oldStatus.priorityOfShuntMap = mapOf(1L to 15)
        staffStatusService.saveStatus(oldStatus)

        val newStatus = oldStatus.copy()
        newStatus.role = StaffAuthority.ROLE_ADMIN
        staffStatusService.replaceStatusWithException(oldStatus, newStatus)

        var check = staffStatusService.assignment(StaffChangeStatusDto(9491, 2, 3))
        StepVerifier.create(check)
            .assertNext {
                assertIterableEquals(listOf(3L), it.userIdList)
            }
            .verifyComplete()

        check = staffStatusService.assignment(StaffChangeStatusDto(9491, 2, 4))
        StepVerifier.create(check)
            .assertNext {
                assertIterableEquals(listOf(3L, 4L), it.userIdList)
            }
            .verifyComplete()
    }

    @Test
    fun testStaffStatus() {
        staffStatusService.saveStatus(
            StaffStatus(
                1, 1L, StaffAuthority.ROLE_STAFF, setOf(3L, 4L), 1,
                 10, 1
            ).also { it.priorityOfShuntMap = mapOf(3L to 15) }
        )
        staffStatusService.saveStatus(
            StaffStatusDto(
                1, 2L, StaffAuthority.ROLE_STAFF, setOf(13L, 4L), 1,
                mapOf(4L to 15), maxServiceCount = 10, staffType = 1
            ).toStaffStatus()
        )

        val staffStatusList = staffStatusService.findIdleStaff(1, 4L)
        println(staffStatusList)

        assertFalse(staffStatusList.isEmpty())

        staffStatusService.setStatusOffline(StaffChangeStatusDto(1, 2L))
        val result = staffStatusService.findIdleStaff(1, 4L)
        println(result)
        assertEquals(1, result.size)

        // 不属于任何接待组的客服
        staffStatusService.saveStatus(
            StaffStatusDto(
                1, 3L, StaffAuthority.ROLE_STAFF, setOf(), 1,
                mapOf(4L to 15), maxServiceCount = 10, staffType = 1
            ).toStaffStatus()
        )
        val allStaff = staffStatusService.findAllOnlineStaff(1).collectList().block()
        println(allStaff)
        assertEquals(1, allStaff?.size)
    }
}