package com.qingzhu.dispatcher.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.dispatcher.component.AssignmentComponent
import com.qingzhu.dispatcher.domain.constant.CloseReason
import com.qingzhu.dispatcher.domain.constant.RelatedType
import com.qingzhu.dispatcher.domain.constant.TransferType
import com.qingzhu.dispatcher.domain.dto.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.Instant

@Service
class AssignmentService(private val assignmentComponent: AssignmentComponent) {
    /**
     * 根据 机构id[organizationId] 和 用户id[userId] 自动分配客服，并返回会话信息
     * TODO: 获取设置的用户信息里的客服ID 和 分组 配置
     */
    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationViewDto> {
        val mono = assignmentComponent.getLastConversationWithCache(organizationId, userId)
        val customerDispatcherDto = assignmentComponent.getCustomerDispatcherWithCache(organizationId, userId)
        // 添加 10 分钟内自动转接人工
        return mono
            .transform { assignmentCustomerAndSendEvent(it) }
            // 分配失败就分配新客服
            .assignmentNewOnError(customerDispatcherDto)
            // 不存在历史会话 或者 重新分配客服失败 就转到机器人客服
            .switchIfEmpty(
                assignmentComponent.getBot(customerDispatcherDto)
                    // 没有机器人在线就分配到人工
                    .switchIfEmpty(assignmentComponent.getStaff(customerDispatcherDto))
            )
            // 保存会话状态
            .transform { assignmentComponent.saveConversation(it) }
            .map {
                ConversationStatusAndViewMapper.mapper.map2View(it)
            }
    }

    /**
     * 用户主动点击转人工
     * 根据 机构id[organizationId] 用户id[userId] 分配人工客服 / 或者进行排队
     */
    fun assignmentStaff(organizationId: Int, userId: Long, fromQueue: Boolean = false): Mono<ConversationViewDto> {
        val customerDispatcherDto = assignmentComponent.getCustomerDispatcherWithCache(organizationId, userId)
        val mono = Mono
            .create<ConversationStatusDto> {
                // TODO 通过 JPA 获取历史会话的座席信息
                // TODO 生成会话信息
                it.success()
            }.cache()
        return mono
            .transform { assignmentCustomerAndSendEvent(it) }
            .onErrorResume {
                // 分配失败就重新分配到其他客服
                assignmentComponent.getStaff(customerDispatcherDto)
                    .doOnNext { cs -> cs.transferType = TransferType.INITIATIVE }
            }
            .switchIfEmpty {
                // 不存在历史座席就分配到新客服
                assignmentComponent.getStaff(customerDispatcherDto)
                    .doOnNext { cs -> cs.transferType = TransferType.INITIATIVE }
            }
            .flatMap {
                // 与机器人会话 进行双向关联
                val endDto = assignmentComponent
                    .getLastConversationWithCache(organizationId, userId)
                    .doOnNext { lc ->
                        lc.closeReason = CloseReason.BOT_TO_STAFF
                        lc.isValid = 1
                        lc.terminator = CreatorType.CUSTOMER
                        lc.endTime = Instant.now()
                    }
                    .map { lc ->
                        it.relatedId = lc.id
                        it.relatedType = RelatedType.FROM_BOT
                        it.visitRange = Duration.between(it.startTime, lc.endTime).toMillis()
                        lc
                    }
                Mono.zip(it.toMono(), endDto)
            }
            .flatMap { zip ->
                // null 的时候丢失了类型信息
                assignmentComponent.saveConversation(zip.t1.toMono())
                    .transform { cv ->
                        cv
                            .map {
                                zip.t2.relatedType = RelatedType.FROM_BOT
                                zip.t2.relatedId = it.id
                                zip.t2
                            }
                            .transform {
                                // 更新机器人会话
                                assignmentComponent.endConversation(it).transform { cv }
                            }
                    }
            }
            .map {
                ConversationStatusAndViewMapper.mapper.map2View(it)
            }
            .switchIfEmpty {
                // getStaff 方法获取客服为空时 进行排队
                if (!fromQueue) {
                    assignmentComponent.pushIntoQueue(customerDispatcherDto)
                } else Mono.empty()
            }
    }

    /**
     * 根据 会话状态[ConversationStatusDto] 更新客服状态
     * 然后更新会话信息
     */
    private fun assignmentCustomerAndSendEvent(mono: Mono<ConversationStatusDto>): Mono<ConversationStatusDto> {
        return mono
            .map { StaffChangeStatusDto(it.organizationId, it.staffId, it.userId) }
            .transform {
                assignmentComponent
                    // 尝试分配到历史客服
                    .assignmentCustomer(it)
                    // 更新会话信息
                    .then(mono)
            }
    }

    /**
     * 分配历史座席失败时，就根据历史信息分配到新客服
     * 会新建一个会话，并与缓存的会话进行关联
     */
    private fun Mono<ConversationStatusDto>.assignmentNewOnError(customerDispatcherDto: Mono<CustomerDispatcherDto>): Mono<ConversationStatusDto> {
        return this
            .onErrorResume {
                this
                    .flatMap {
                        if (it.interaction == 1) {
                            // 客服会话
                            assignmentComponent
                                .getStaff(customerDispatcherDto)
                                .doOnNext { cs ->
                                    // 设置为主动转人工
                                    cs.transferType = TransferType.INITIATIVE
                                }
                        } else {
                            // 机器人会话
                            assignmentComponent.getBot(customerDispatcherDto)
                        }
                    }
                    .flatMap {
                        // 进行会话关联
                        // 历史会话不进行双向关联
                        this
                            .map { cv ->
                                it.relatedId = cv.id
                                it.relatedType = RelatedType.FROM_HISTORY
                                it.visitRange = Duration.between(it.startTime, cv.endTime).toMillis()
                                it
                            }
                    }
                    // 如果分配到新客服也失败了，就返回空对象，后续直接分配到机器人
                    .onErrorResume { Mono.empty() }
            }
    }
}