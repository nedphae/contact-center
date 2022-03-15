package com.qingzhu.messageserver.domain.dto

data class Customer(
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int,
    /**
     * 用户id
     */
    val uid: String,
    /** 用户姓名 */
    var name: String?,
    /** 用户邮箱 */
    var email: String?,
    /** 用户手机号 */
    var mobile: String?,
    /** vip等级 1-10 */
    var vipLevel: Int?,
)