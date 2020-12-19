package com.qingzhu.messageserver.domain.entity

import com.qingzhu.messageserver.domain.constant.BusyStatus
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.constant.ReadyStatus
import com.qingzhu.messageserver.domain.constant.StaffRole
import java.util.*


data class StaffStatus(
        // 公司id
        val organizationId: Int,
        // 客服id
        // 每个客服只能保存一个状态
        val staffId: Long,
        // 角色种类
        var role: StaffRole,
        // 所处接待组
        var receptionistGroup: List<Long>,
        // 不同接待组的优先级
        var priorityOfGroup: Map<Long, Int>,
        // 客服所处服务器 hash 值
        var redisHashKey: Int = -1,
        // 最大接待数量
        var maxServiceCount: Int
) : java.io.Serializable {
    // 登录时间
    val loginTime: Date = Date()

    // 在线状态
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE

    // 就绪状态
    var readyStatus: ReadyStatus = ReadyStatus.READY
        set(value) {
            // 必须 在线状态才能设置就绪
            if (field == ReadyStatus.UNREADY && onlineStatus == OnlineStatus.OFFLINE)
                throw UnsupportedOperationException("客服未在线，无法设置就绪")
            field = value
        }

    // 繁忙状态
    var busyStatus: BusyStatus = BusyStatus.IDLE
        set(value) {
            // 必须 就绪状态才能设置空闲
            if (readyStatus == ReadyStatus.UNREADY)
                throw UnsupportedOperationException("客服未就绪，无法设置示忙")
            field = value
        }

    // 当前接待量 (不能大于最大接待量)
    var currentServiceCount: Int = 0
        set(value) {
            if (value >= maxServiceCount) {
                autoBusy = true
            }
            field = value
        }

    // 自动忙碌(当当前接待量大于等于最大接待量时)
    var autoBusy: Boolean = false
        // 只能自动设置
        private set

    /**
     * 服务的用户id
     */
    val userIdList: MutableList<Long> = ArrayList(maxServiceCount)

    fun setOffline() = apply { this.onlineStatus = OnlineStatus.OFFLINE }
}