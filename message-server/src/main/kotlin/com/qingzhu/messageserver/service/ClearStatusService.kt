package com.qingzhu.messageserver.service

import com.hazelcast.core.EntryEvent
import com.hazelcast.map.listener.EntryExpiredListener
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import com.qingzhu.messageserver.domain.entity.StaffStatus
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

/**
 * 清理机器人会话残留
 */
@Component
class ClearStatusService(
    private val conversationStatusService: ConversationStatusService,
) : EntryExpiredListener<String, CustomerStatus> {
    /**
     * 清理机器人会话残留
     * Invoked upon expiration of an entry.
     *
     * @param event the event invoked when an entry is expired.
     */
    override fun entryExpired(event: EntryEvent<String, CustomerStatus>) {
        val customerStatus = event.value
        if (customerStatus != null) {
            conversationStatusService.findLatestByUserId(
                customerStatus.organizationId,
                customerStatus.userId
            )
                .filter { it.interaction == 0 }
                // 机器人会话超时 持久化会话信息
                .doOnNext {
                    it.terminator = CreatorType.SYS
                }
                .flatMap {
                    conversationStatusService.endConversation(it)
                }
                .subscribe()
        }
    }
}