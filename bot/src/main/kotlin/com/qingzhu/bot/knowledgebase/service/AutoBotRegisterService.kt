package com.qingzhu.bot.knowledgebase.service

import com.hazelcast.core.HazelcastInstance
import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import org.slf4j.LoggerFactory
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
    companion object {
        private val logger =  LoggerFactory.getLogger(AutoBotRegisterService::class.java)
    }

    /**
     * 自动注册机器人服务
     */
    // @Scheduled(fixedDelayString = "PT30M")
    override fun run(vararg args: String?) {
        // 使用 single 线程池
        Flux.interval(Duration.ZERO, Duration.ofMinutes(30), Schedulers.single())
            .map { hazelcastInstance.cpSubsystem.getLock("bot") }
            .filter { it.tryLock() }
            .flatMap { staffAdminService.findAllEnabledBotStaff() }
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
            .retryWhen(
                Retry.fixedDelay(80, Duration.ofSeconds(10))
                    .doBeforeRetry {
                        logger.error("注册机器人异常，进行重试", it.failure())
                    }
                //.filter { e -> e is ConnectTimeoutException || e is WebClientException }
            )
            .subscribe()
        // 由 hazelcast 自己检查关闭
        // lock.unlock()
    }
}