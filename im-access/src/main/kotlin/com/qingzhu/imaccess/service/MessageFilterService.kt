package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.AbstractSpecification
import com.qingzhu.common.domain.shared.Specification
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import com.qingzhu.imaccess.util.ParserUtils
import com.qingzhu.imaccess.util.TimeKey
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
        filters.stream().toList().associateBy { it::class.simpleName!!.toLowerCase() }

    /**
     * 过滤表达式，后期可以读取配置
     */
    private val expression = "MessageToFilter AND CustomerFilter"

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

/**
 * 客户特定时间没有说话就踢出咨询，记录客户消息时间
 */
@Service
class CustomerFilter : AbstractSpecification<Message>() {
    override fun isSatisfiedBy(t: Message): Boolean {
        if (t.creatorType == CreatorType.CUSTOMER && t.organizationId != null) {
            // 是用户消息就标记 时间
            MapUtils.Time.markTimeByKey(TimeKey(CreatorType.CUSTOMER, t.from!!).apply { organizationId = t.organizationId!! })
        }
        return true
    }
}