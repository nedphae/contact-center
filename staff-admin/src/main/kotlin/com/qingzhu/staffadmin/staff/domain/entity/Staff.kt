package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.staffadmin.staff.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.AbstractAuditingEntity
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

/**
 * 客服登录用户
 * 替换现有系统的 user 用户表，
 * 使用 OAuth2 做用户认证
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["organizationId", "username"])],
        indexes = [Index(columnList = "organizationId"),
            Index(columnList = "username"), Index(columnList = "staffGroupId")])
data class Staff(
        // 公司id
        val organizationId: Int,
        // 用户名
        val username: String,
        // 密码
        var password: String,
        // 角色
        var role: StaffAuthority,
        // 所属分组
        // @ManyToOne
        var staffGroupId: Long
) : AbstractAuditingEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(columnDefinition = "serial")
    var id: Long? = null

    // 实名
    lateinit var realName: String

    // 昵称
    lateinit var nickName: String

    // 同时接待量（默认设置为8）
    var simultaneousService: Int = 8

    // 工单
    // 每日上限
    var maxTicketPerDay: Int = 999

    // 总上限
    var maxTicketAllTime: Int = 999

    // 是否是机器人 0 机器人， 1人工
    val staffType: Int = 1

    // 性别
    var gender: Int = 0

    // 手机
    var mobilePhone: String? = null

    // 个性签名
    var personalizedSignature: String? = null
}