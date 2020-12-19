package com.qingzhu.staffadmin.staff.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(indexes = [Index(columnList = "organizationId"),
    Index(columnList = "staffId"), Index(columnList = "shuntId")])
data class StaffConfig(
        // 公司id
        val organizationId: Int,
        // 配置的客服 (每个客服可以有多个配置)
        // @ManyToOne
        val staffId: Long,
        // 接待组 id
        val shuntId: Long,
        // 配置优先级
        var priority: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(columnDefinition = "serial")
    var id: Long? = null

    @CreatedDate
    var createTime: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updateTime: LocalDateTime = LocalDateTime.now()
}