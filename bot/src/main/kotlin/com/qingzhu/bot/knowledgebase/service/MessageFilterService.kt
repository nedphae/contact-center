package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.dto.MessagePair
import com.qingzhu.common.domain.shared.AbstractSpecification
import com.qingzhu.common.domain.shared.Specification
import com.qingzhu.bot.util.ParserUtils
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.streams.toList

/**
 * 使用 Specification 接口实现做过滤
 * 已经实现 括号表达式
 */
@Service
class MessageFilterService(
    filters: ObjectProvider<AbstractSpecification<MessagePair>>,
) {
    private val filterMap: Map<String, Specification<MessagePair>> =
        filters.stream().toList().associateBy { it::class.simpleName!!.toLowerCase() }

    /**
     * 过滤表达式，后期可以读取配置
     */
    private val expression = "SyncMessageFilter"

    private val filterChain = ParserUtils(expression) {
        filterMap[it] ?: object : AbstractSpecification<MessagePair>() {
            //如果不存在特定名称的过滤器就返回默认 true 的过滤器
            override fun isSatisfiedBy(t: MessagePair): Boolean {
                return true
            }
        }
    }.calExpression()

    fun filter(messagePair: Mono<MessagePair>): Mono<MessagePair> {
        return messagePair.filter(filterChain::isSatisfiedBy)
    }
}

@Service
class SyncMessageFilter(
    private val messageService: MessageService,
) : AbstractSpecification<MessagePair>() {
    override fun isSatisfiedBy(t: MessagePair): Boolean {
        // 同步机器人聊天消息
        Flux.just(t.questionMessage, *t.answerMessage.toTypedArray())
            .flatMap(messageService::sync)
            .subscribe()
        return true
    }
}
