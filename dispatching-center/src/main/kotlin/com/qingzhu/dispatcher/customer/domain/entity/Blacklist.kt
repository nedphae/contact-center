package com.qingzhu.dispatcher.customer.domain.entity

import com.qingzhu.common.domain.AbstractStaffEntity
import com.qingzhu.dispatcher.customer.domain.constant.PreventStrategy
import java.time.Instant

/**
 * 黑名单仅仅保存在 redis 里面
 * 如果需要持久化，请开启 AOF
 */
data class Blacklist(
    val preventStrategy: PreventStrategy,
    val preventSource: String,
    /** 有效时间 */
    var effectiveTime: Instant? = null,
    /** 有效期结束 */
    var failureTime: Instant? = null,
    // 是否经过审计
    var audited: Boolean = true,
): AbstractStaffEntity()