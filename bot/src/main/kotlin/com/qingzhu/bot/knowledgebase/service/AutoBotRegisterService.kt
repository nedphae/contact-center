package com.qingzhu.bot.knowledgebase.service

import com.hazelcast.core.HazelcastInstance
import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import io.netty.channel.ConnectTimeoutException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
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

    /**
     * 自动注册机器人服务
     */
    // @Scheduled(fixedDelayString = "PT30M")
    override fun run(vararg args: String?) {
        // 使用 single 线程池
        Flux.interval(Duration.ZERO, Duration.ofMinutes(30), Schedulers.single())
            .map { hazelcastInstance.cpSubsystem.getLock("bot") }
            .filter { it.tryLock() }
            .flatMap {
                staffAdminService.findAllEnabledBotStaff()
                    .retryWhen(
                        Retry.fixedDelay(50, Duration.ofSeconds(5))
                            .filter { e -> e is ConnectTimeoutException }
                    )
            }
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
        // 由 hazelcast 自己检查关闭
        // lock.unlock()
    }
}