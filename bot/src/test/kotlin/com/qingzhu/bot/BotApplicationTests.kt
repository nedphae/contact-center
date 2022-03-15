package com.qingzhu.bot

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.cp.CPSubsystem
import com.hazelcast.cp.lock.FencedLock
import com.qingzhu.bot.config.HazelcastClientConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootTest
@ComponentScan(
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE, classes = [HazelcastClientConfig::class]
        )]
)
class BotApplicationTests {

    @MockBean
    private lateinit var newHazelcastClient: HazelcastInstance

    @MockBean
    private lateinit var cpSubsystem: CPSubsystem

    @MockBean
    private lateinit var fencedLock: FencedLock

    @BeforeEach
    fun init() {
        Mockito.`when`(fencedLock.tryLock()).thenReturn(false)
        Mockito.`when`(cpSubsystem.getLock(ArgumentMatchers.anyString())).thenReturn(fencedLock)
        Mockito.`when`(newHazelcastClient.cpSubsystem).thenReturn(cpSubsystem)
    }

    @Test
    fun contextLoads() {
    }

}
