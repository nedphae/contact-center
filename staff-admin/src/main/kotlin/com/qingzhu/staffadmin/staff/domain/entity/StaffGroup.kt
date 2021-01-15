package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.staffadmin.staff.domain.AbstractAuditingEntity
import javax.persistence.*

/**
 * 客服分组
 */
@Entity
@Table(indexes = [Index(columnList = "organizationId"), Index(columnList = "groupName")])
data class StaffGroup(
        // 公司id
        val organizationId: Int
) : AbstractAuditingEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // 部门名称
    lateinit var groupName: String

    // @OneToMany(mappedBy = "staffGroup", fetch = FetchType.LAZY)
    // lateinit var staffList: MutableSet<Staff>
}