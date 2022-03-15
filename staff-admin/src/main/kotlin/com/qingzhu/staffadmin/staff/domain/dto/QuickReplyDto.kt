package com.qingzhu.staffadmin.staff.domain.dto

data class AllQuickReplyDto(
    val withGroup: List<QuickReplyGroupDto>?,
    val noGroup: List<QuickReplyDto>?,
)

data class QuickReplyGroupDto(
    var id: Long,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    val groupName: String,
    val personal: Boolean,
    var quickReply: List<QuickReplyDto>?,
)

data class QuickReplyDto(
    var id: Long,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    val groupId: Long?,
    val title: String,
    val content: String,
    val personal: Boolean,
)
