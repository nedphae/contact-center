package com.qingzhu.staffadmin.staff.domain.entity

import javax.persistence.*


/**
 * 接待组 aka 技能组
 */
@Entity
@Table(indexes = [Index(columnList = "organizationId"),
    Index(columnList = "receptionistGroupClassId")])
data class Shunt(
        // 公司id
        val organizationId: Int,
        // 接待组 名称
        val name: String,
        // 接待组所属分类
        // @ManyToOne
        val shuntClassId: Long,
        // 接待组范围代码
        val code: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // 技能组
    // @OneToMany(fetch = FetchType.LAZY)
    // @JoinColumn(name = "receptionist_group_id")
    // lateinit var staffConfigList: MutableSet<StaffConfig>
}