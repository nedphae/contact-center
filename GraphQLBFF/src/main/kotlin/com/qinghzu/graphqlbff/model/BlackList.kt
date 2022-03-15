package com.qinghzu.graphqlbff.model

data class Blacklist(
    val preventStrategy: String,
    val preventSource: String,
    /** 有效时间 */
    var effectiveTime: Double? = null,
    /** 有效期结束 */
    var failureTime: Double? = null,
    // 是否经过审计
    var audited: Boolean = true,
): AbstractStaffEntity() {
    val id: String
        get() = "${this.organizationId}:${this.preventStrategy}:${this.preventSource}"
}

data class BlacklistInput(
    val preventStrategy: String,
    val preventSource: String,
    /** 有效时间 */
    var effectiveTime: Double? = null,
    /** 有效期结束 */
    var failureTime: Double? = null,
    // 是否经过审计
    var audited: Boolean? = true,
)