package com.qingzhu.messageserver.service

import com.hazelcast.core.EntryEvent
import com.hazelcast.map.listener.EntryExpiredListener
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.messageserver.domain.entity.StaffStatus
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

/**
 * 清理会话残留，redis 缓存记录
 */
@Component
class ClearStatusService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) : EntryExpiredListener<String, StaffStatus> {
    /**
     * 清理客服聊天缓存
     * Invoked upon expiration of an entry.
     *
     * @param event the event invoked when an entry is expired.
     */
    override fun entryExpired(event: EntryEvent<String, StaffStatus>) {
        val staffStatus = event.value
        if (staffStatus != null) {
            val redisKey =
                "${staffStatus.organizationId}:${CreatorType.STAFF.name.toLowerCase()}:${staffStatus.staffId}"
            redisTemplate.delete(redisKey)
        }
    }
}