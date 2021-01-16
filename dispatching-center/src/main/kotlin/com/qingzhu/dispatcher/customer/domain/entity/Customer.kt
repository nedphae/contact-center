package com.qingzhu.dispatcher.customer.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

// @Entity
// @Table(uniqueConstraints = [UniqueConstraint(columnNames = ["organizationId", "uid"])],
//         indexes = [Index(columnList = "organizationId"),
//             Index(columnList = "uid"), Index(columnList = "mobile")])
@Table
data class Customer(
        // 公司id
        val organizationId: Int,
        /**
         * 用户id
         * 用户在企业产品中的标识，便于后续客服系统中查看该用户在产品中的相关信息，
         * 不传表示匿名用户 。若要指定用户信息，不显示默认的（guestxxx用户姓名），就必须传uid
         */
        val uid: String,
        // 用户姓名
        var name: String?,
        // 用户邮箱
        var email: String?,
        // 用户手机号
        var mobile: String?,
        // vip等级 1-10
        var vipLevel: Int?
) {
    @Id
    var id: Long? = null
}