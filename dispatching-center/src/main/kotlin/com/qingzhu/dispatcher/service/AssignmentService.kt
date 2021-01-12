package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.constant.RelatedType
import com.qingzhu.dispatcher.domain.constant.TransferType
import com.qingzhu.dispatcher.domain.dto.*
import com.qingzhu.dispatcher.service.impl.WeightedAssignmentService
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Service
class AssignmentService(
        private val messageService: MessageService,
        private val staffAdminService: StaffAdminService,
        private val weightedAssignmentService: WeightedAssignmentService,
        redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val listOps: ReactiveListOperations<String, String> = redisTemplate.opsForList()

    private fun getLastConversation(organizationId: Int, userId: Long): Mono<ConversationView> {
        return messageService.findConversationByUserId(organizationId, userId)
                .cache()
    }

    /**
     * TODO: 获取设置的用户信息里的客服ID 和 分组 配置
     */
    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = getLastConversation(organizationId, userId)
        // 添加 10 分钟内自动转接人工
        return mono
                .doOnNext {
                    // 尝试分配到历史客服
                    messageService.assignmentCustomer(StaffChangeStatusDto(it.organizationId,
                            it.staffId!!, it.userId).toMono())
                }
                .onErrorResume { mono }
                .flatMap {
                    if (it.interaction == 0) {
                        // 客服会话
                        staff(it.organizationId, it.userId)
                                .doOnNext { cs ->
                                    // 设置为主动转人工
                                    cs.transferType = TransferType.INITIATIVE
                                }
                    } else {
                        // 机器人会话
                        bot(it.organizationId, it.userId)
                    }
                }
                // 不存在历史会话 或者 重新分配客服失败 就转到机器人客服
                .switchIfEmpty(bot(organizationId, userId))
                .flatMap {
                    // 进行会话关联
                    // 历史会话不进行双向关联
                    mono
                            .map { cv ->
                                it.relatedId = cv.id
                                it.relatedType = RelatedType.FROM_HISTORY
                                it.visitRange = Duration.between(it.startTime, cv.endTime).toMillis()
                                it
                            }
                }
                .transform {
                    messageService.createConversation(it)
                }
    }

    private fun bot(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleBotStaff(organizationId, it) }
    }

    private fun assignment(organizationId: Int, userId: Long,
                           getStaffList: (shuntId: Long) ->
                           Flux<StaffDispatcherDto>): Mono<ConversationStatusDto> {
        val customerDispatcherDto = getCustomerDispatcher(organizationId, userId)
        return customerDispatcherDto
                .map { getStaffList(it.shuntId) }
                .cache()
                .flatMap {
                    // 根据权重分配客服
                    weightedAssignmentService.assignmentStaff(it)
                }
                .flatMap {
                    val dto = StaffChangeStatusDto(organizationId, it!!, userId).toMono()
                    // 发送分配请求给客服 如果失败就重新分配
                    messageService.assignmentCustomer(dto)
                    dto
                }
                .retry(3)
                .flatMap {
                    staffAdminService.getStaffInfo(it.organizationId, it.staffId)
                }
                .flatMap {
                    customerDispatcherDto
                            .map { cd ->
                                ConversationStatusDto.fromStaffAndCustomer(it!!,
                                        // it.staffType.inv()
                                        // 会话类型 和 客服类型相同
                                        cd!!, it.staffType)
                            }
                }
    }

    private fun getCustomerDispatcher(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
                .cache()
                .filter { it.shuntId != -1L }
    }

    private fun staff(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleStaff(organizationId, it) }
    }


    /**
     * 手动从机器人转人工
     */
    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = Mono
                .create<ConversationStatusDto> {
                    // 通过 JPA 获取历史会话的座席信息
                    it.success()
                }.cache()
        return mono
                .doOnNext {
                    // 尝试分配到历史客服
                    messageService.assignmentCustomer(StaffChangeStatusDto(it.organizationId,
                            it.staffId, it.userId).toMono())
                }
                .onErrorResume {
                    // 分配失败就重新分配到其他客服
                    staff(organizationId, userId)
                            .doOnNext { cs -> cs.transferType = TransferType.INITIATIVE }
                }
                .flatMap {
                    // 与机器人会话 进行双向关联
                    val endDto = getLastConversation(organizationId, userId)
                            .map { cv ->
                                ConversationEndDto.createById(cv.organizationId, cv.id!!)
                            }
                            .map { ce ->
                                it.relatedId = ce.id
                                it.relatedType = RelatedType.FROM_BOT
                                it.visitRange = Duration.between(it.startTime, ce.endTime).toMillis()
                                ce
                            }
                    Mono.zip(it.toMono(), endDto)
                }
                .flatMap { zip ->
                    // null 的时候丢失了类型信息
                    messageService.createConversation(zip.t1.toMono())
                            .transform { cv ->
                                cv
                                        .map {
                                            zip.t2.relatedId = it.id
                                            zip.t2
                                        }
                                        .transform {
                                            // 更新机器人会话
                                            messageService.endConversation(it).transform { cv }
                                        }
                            }
                }
                .switchIfEmpty {
                    // staff 方法获取客服为空时 进行排队
                    Mono
                            .just(userId)
                            .flatMap {
                                // 客户 插入到 redis 列队
                                listOps.leftPush("queue:$organizationId", it.toString())
                            }
                            .map { ConversationView(organizationId, userId, it) }
                }
    }
}