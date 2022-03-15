package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import com.qingzhu.common.domain.shared.msg.dto.HistoryResult
import com.qingzhu.imaccess.domain.dto.CustomerBaseStatusDto
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.view.ConversationView
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomerAccessService(
    private val registerService: RegisterService,
    private val dispatchingCenter: DispatchingCenter,
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
) {
    suspend fun register(customerConfig: CustomerConfig): ConversationView {
        val shuntDto = staffAdminService.getShuntByCode(customerConfig.shuntId).awaitSingleOrNull()
        return if (shuntDto == null) {
            ConversationView(errorCode = 404, errorMessage = "没有找到接待组")
        } else {
            // del 检查 是否在缓存中 缓存10分钟 del
            // 缓存中存在客户信息就重新注册
            val customerStatusDto = registerService.registerCustomer(customerConfig, shuntDto).awaitSingle()
            val customer = CustomerBaseStatusDto(shuntDto.organizationId, customerStatusDto.userId)
            // 获取 客服预分配信息，bot http 转 websocket 如果转人工 然后分配失败就体验不好了
            // 所以使用 预先分配的客服
            dispatchingCenter.assignmentAuto(customer.organizationId, customer.userId).awaitSingle().also {
                val historyMsg = messageService.loadHistoryMessage(customer.organizationId, customer.userId, null, 5).awaitSingle().content
                it.config = shuntDto.config
                it.historyMsg = historyMsg.map(ChatUIMessage::fromMessage).reversed()
            }
        }
    }

    fun hasHistoryMessage(organizationId: Int, userId: Long): Mono<Boolean> {
        return messageService.hasHistoryMessage(organizationId, userId)
    }

    fun loadHistoryMessage(organizationId: Int, userId: Long, lastSeqId: Long?, pageSize: Int?): Mono<HistoryResult> {
        return messageService.loadHistoryMessage(organizationId, userId, lastSeqId, pageSize)
            .map {
                HistoryResult(
                    it.lastOrNull()?.seqId,
                    it.content.map(ChatUIMessage::fromMessage).reversed(),
                    it.hasNext()
                )
            }
    }
}