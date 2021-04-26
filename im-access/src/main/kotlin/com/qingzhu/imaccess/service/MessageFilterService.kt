package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.AbstractSpecification
import com.qingzhu.common.domain.shared.Specification
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.util.ParserUtils
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import kotlin.streams.toList

/**
 * 使用 Specification 接口实现做过滤
 * 已经实现 括号表达式
 */
@Service
class MessageFilterService(
        filters: ObjectProvider<Specification<Message>>
) {
    private val filterMap: Map<String, Specification<Message>> =
            filters.stream().toList().map { it::class.simpleName!!.toLowerCase() to it }.toMap()

    /**
     * 过滤表达式，后期可以读取配置
     */
    private val expression = "MessageToFilter"

    private val filterChain = ParserUtils(expression) {
        filterMap[it] ?: object : AbstractSpecification<Message>() {
            //如果不存在特定名称的过滤器就返回默认 true 的过滤器
            override fun isSatisfiedBy(t: Message): Boolean {
                return true
            }
        }
    }.calExpression()

    fun filter(message: Mono<Message>): Mono<Message> {
        return message.filter(filterChain::isSatisfiedBy)
    }
}

@Service
class MessageToFilter : AbstractSpecification<Message>() {
    override fun isSatisfiedBy(t: Message): Boolean {
        return t.to != null
    }
}