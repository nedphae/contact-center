package com.qingzhu.dispatcher.customer.domain.entity

import com.qingzhu.common.domain.AbstractStaffEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table
data class BlackList(
    @Id
    var id: Long? = null,
    val userId: Long,
    /** 有效时间 */
    var effectiveTime: Instant?,
    /** 有效期结束 */
    var failureTime: Instant?,
): AbstractStaffEntity()
