package com.qingzhu.dispatcher.customer.service

import com.qingzhu.dispatcher.DispatcherApplicationTests
import com.qingzhu.dispatcher.customer.domain.constant.PreventStrategy
import com.qingzhu.dispatcher.customer.domain.entity.Blacklist
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

internal class BlacklistServiceTest : DispatcherApplicationTests() {
    @Autowired
    private lateinit var blacklistService: BlacklistService

    @Test
    fun testSaveBlacklist() {
        runBlocking {
            val result = blacklistService.saveBlacklist(
                flowOf(
                    Blacklist(PreventStrategy.UID, "guest_89daca8b"),
                    // Blacklist(PreventStrategy.UID, "gust_test_2"),
                    // Blacklist(PreventStrategy.UID, "gust_test_3"),
                    // Blacklist(PreventStrategy.UID, "gust_test_4"),
                    // Blacklist(PreventStrategy.UID, "gust_test_5"),
                    // Blacklist(PreventStrategy.UID, "gust_test_6"),
                    // Blacklist(PreventStrategy.UID, "gust_test_7"),
                ).onEach { it.organizationId = 9491; it.staffId = 1 })
                .toList()
            println(result)
        }

    }
}