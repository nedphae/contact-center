package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.dto.StaffStatusDto
import io.netty.channel.ConnectTimeoutException
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import java.time.Duration

/**
 * 自动注册机器人，由获取到锁的服务注册
 */
@Service
class AutoBotRegisterService(
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        messageService.getBotLock()
            //超时错误重试
            .retryWhen(
                Retry.fixedDelay(50, Duration.ofSeconds(10))
                    .filter { e -> e is ConnectTimeoutException }
            )
            .filter { it == true }
            .flatMap {
                // 获取机器人信息
                staffAdminService.findAllEnabledBotStaff()
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
                )
                messageService.registerStaff(staffStatusDto.toMono())
            }
            .doFinally {
                messageService.releaseBotLock().subscribe()
            }
            .subscribe()
    }
}