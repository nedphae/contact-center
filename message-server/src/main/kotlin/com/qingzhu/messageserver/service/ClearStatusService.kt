package com.qingzhu.messageserver.service

import com.hazelcast.core.EntryEvent
import com.hazelcast.map.listener.EntryExpiredListener
import com.qingzhu.messageserver.domain.entity.CustomerStatus
import org.springframework.stereotype.Component

@Component
class ClearStatusService : EntryExpiredListener<String, CustomerStatus> {
    /**
     * Invoked upon expiration of an entry.
     *
     * @param event the event invoked when an entry is expired.
     */
    override fun entryExpired(event: EntryEvent<String, CustomerStatus>) {
        TODO("Not yet implemented")
    }
}