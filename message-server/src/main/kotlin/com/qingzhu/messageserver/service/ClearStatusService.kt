package com.qingzhu.messageserver.service

import com.hazelcast.core.EntryEvent
import com.hazelcast.map.listener.EntryExpiredListener
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import org.springframework.stereotype.Component

/**
 * 清理会话残留，redis 缓存记录，更新 持久化 会话信息
 */
@Component
class ClearStatusService : EntryExpiredListener<String, ConversationStatus> {
    /**
     * Invoked upon expiration of an entry.
     *
     * @param event the event invoked when an entry is expired.
     */
    override fun entryExpired(event: EntryEvent<String, ConversationStatus>) {
        TODO("Not yet implemented")
    }
}