package com.qingzhu.bot.knowledgebase.service

import com.hazelcast.core.HazelcastInstance
import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import io.netty.channel.ConnectTimeoutException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import java.time.Duration

/**
 * 自动注册机器人，由获取到分布式锁的其中一个bot服务注册
 */
@Service
class AutoBotRegisterService(
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
    @Qualifier("newHazelcastClient") private val hazelcastInstance: HazelcastInstance,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val lock = hazelcastInstance.cpSubsystem.getLock("bot")
        if (lock.tryLock()){
            try {
                // 获取机器人信息
                staffAdminService.findAllEnabledBotStaff()
                    //超时错误重试
                    .retryWhen(
                        Retry.fixedDelay(50, Duration.ofSeconds(5))
                            .filter { e -> e is ConnectTimeoutException }
                    )
                    .flatMap {
                        //注册机器人信息
                        val staffStatusDto = StaffStatusDto(
                            organizationId = it.organizationId,
                            staffId = it.id,
                            role = it.role,
                            shunt = it.shunt,
                            priorityOfShunt = it.priorityOfShunt,
                            maxServiceCount = it.simultaneousService,
                            staffType = it.staffType
                        )
                        messageService.registerStaff(staffStatusDto.toMono())
                    }
                    .subscribe()
            } finally {
                lock.unlock()
            }
        }
    }
}