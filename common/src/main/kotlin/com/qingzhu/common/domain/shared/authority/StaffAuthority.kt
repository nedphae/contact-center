package com.qingzhu.common.domain.shared.authority

/**
 * Spring 权限默认需要 ROLE_ 开头
 */
enum class StaffAuthority {
    // 管理员
    ROLE_ADMIN,

    // 客服
    ROLE_STAFF,

    // 组长
    ROLE_LEADER,

    // 质检
    ROLE_QA,

    // 开放权限 或者 PreAuthorize 为空
    ROLE_ALL,

    // 客户
    ROLE_C
}