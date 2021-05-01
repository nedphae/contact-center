package com.qingzhu.messageserver.service

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CPSubsystemService(
    @Qualifier("hazelcastInstance")
    private val hazelcastInstance: HazelcastInstance
) {
    private final val cpSubsystem = hazelcastInstance.cpSubsystem

    val lock = cpSubsystem.getLock("bot")

    fun getBotLock(): Boolean {
        return lock.tryLock()
    }

    fun releaseBotLock() {
        lock.unlock()
    }
}