package com.qingzhu.imaccess.config

import com.lmax.disruptor.BlockingWaitStrategy
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.WorkHandler
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ExceptionHandlerWrapper
import com.lmax.disruptor.dsl.ProducerType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PreDestroy

@Configuration
class DisruptorConfig {
    private val atomicInt = AtomicInteger()

    private lateinit var disruptor: Disruptor<DisruptorEvent>

    @Bean
    fun initChatMessageDisruptor(messageHandler: WorkHandler<DisruptorEvent>): Disruptor<DisruptorEvent> {
        /** 指定RingBuffer的大小 **/
        // TODO 后期使用配置
        val bufferSize = 1024
        val threads = 20
        // 生产者的线程工厂
        val threadFactory = ThreadFactory { r -> Thread(r, "DefaultDisruptorThreads" + atomicInt.getAndIncrement()) }
        // RingBuffer生产工厂,初始化RingBuffer的时候使用
        val factory = EventFactory { DisruptorEvent() }
        // 阻塞策略
        val strategy = BlockingWaitStrategy()
        val disruptor = Disruptor(factory, bufferSize, threadFactory, ProducerType.MULTI, strategy)
        val array = Array(threads) { messageHandler }
        disruptor.handleEventsWithWorkerPool(*array)
        disruptor.setDefaultExceptionHandler(ExceptionHandlerWrapper())
        disruptor.start()
        this.disruptor = disruptor
        return disruptor
    }

    @PreDestroy
    fun stopDisruptor() {
        this.disruptor.shutdown()
    }
}

