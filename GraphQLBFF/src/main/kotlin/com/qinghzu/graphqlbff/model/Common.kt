package com.qinghzu.graphqlbff.model

abstract class AbstractAuditingEntity {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    /** 机构 ID **/
    var organizationId: Int? = null
}

abstract class AbstractStaffEntity {
    /** 机构 ID **/
    var organizationId: Int? = null
    // 配置的客服
    var staffId: Long? = null
}