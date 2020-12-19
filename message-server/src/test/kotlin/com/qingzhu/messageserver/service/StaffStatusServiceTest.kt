package com.qingzhu.messageserver.service

import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.constant.StaffRole
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.entity.StaffStatus
import com.qingzhu.messageserver.domain.dto.StaffStatusDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired


class StaffStatusServiceTest : MessageServerApplicationTests() {
    @Autowired
    private lateinit var staffStatusService: StaffStatusService

    @Test
    fun testStaffStatus() {
        staffStatusService.saveStatus(StaffStatus(1, 2L, StaffRole.STAFF, listOf(3L, 4L),
                mapOf(3L to 15), 1234, 10))
        staffStatusService.saveStatus(StaffStatusDto(1, 12L, StaffRole.STAFF, listOf(13L, 4L),
                mapOf(4L to 15)).toStaffStatus())

        val staffStatusList = staffStatusService.findIdleStaff(1, 4L)
        println(staffStatusList)

        assertFalse(staffStatusList.isEmpty())

        staffStatusService.setStatusOffline(StaffChangeStatusDto(1, 2L, null))
        val result = staffStatusService.findIdleStaff(1, 4L)
        println(result)
        assertEquals(1, result.size)
    }
}