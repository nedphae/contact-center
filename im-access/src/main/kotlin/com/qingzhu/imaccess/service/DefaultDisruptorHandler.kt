package com.qingzhu.imaccess.service

import com.lmax.disruptor.WorkHandler
import com.qingzhu.imaccess.config.DisruptorEvent
import com.qingzhu.imaccess.config.EventType
import com.qingzhu.imaccess.service.disruptor.ConvDisruptorHandler
import com.qingzhu.imaccess.service.disruptor.MessageDisruptorHandler
import org.springframework.stereotype.Service

/**
 * disruptor 消息处理策略类
 */
@Service
class DefaultDisruptorHandler(
    private val messageDisruptorHandler: MessageDisruptorHandler,
    private val convDisruptorHandler: ConvDisruptorHandler,
) : WorkHandler<DisruptorEvent> {
    /**
     * 表示需要处理的工作单元的回调。
     *
     * @param event 发布到 [com.lmax.disruptor.RingBuffer] 的事件
     * @throws Exception 是否 WorkHandler 希望异常在链的更深处进行处理.
     */
    override fun onEvent(event: DisruptorEvent) {
        when (val eventType = event.type) {
            is EventType.Msg -> messageDisruptorHandler.onEvent(eventType.deserializeToPair())
            is EventType.Conv -> convDisruptorHandler.onEvent(eventType.deserializeToPair())
            is EventType.None -> eventType.deserializeToPair()
        }
    }
}