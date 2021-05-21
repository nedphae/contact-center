package com.qingzhu.messageserver.domain.entity

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import java.time.Instant

data class StaffStatus(
    /** 公司id */
    val organizationId: Int,
    /** 客服id
     *
     * 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: StaffAuthority,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    val staffType: Int = 1,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /**
     * 上一条接受的消息ID，或者事件序列ID
     * 用以检查是否漏收了消息
     */
    var pts: Long? = null,
) : java.io.Serializable {
    /** 登录时间 */
    val loginTime: Instant = Instant.now()

    /**
     * 服务的用户id
     */
    val userIdList: MutableList<Long> = ArrayList(maxServiceCount)

    /** 客服所处服务器名称 */
    val clientAccessServerMap: MutableMap<String, String> = HashMap()

    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    /** 当前接待量 (不能大于最大接待量) */
    var currentServiceCount: Int = 0
        set(value) {
            autoBusy = value >= maxServiceCount
            field = value
        }

    /** 自动忙碌(当当前接待量大于等于最大接待量时) */
    var autoBusy: Boolean = false
        // 只能自动设置
        private set


    fun setOffline() = apply { this.onlineStatus = OnlineStatus.OFFLINE }
}