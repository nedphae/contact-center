package com.qingzhu.messageserver.domain.entity

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.messageserver.domain.constant.FromType
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import java.time.Instant

data class CustomerStatus(
    /** 公司id */
    val organizationId: Int,
    /** 客户系统id */
    val userId: Long,
    /** 客户提交id */
    val uid: String?,
    /** 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用 */
    val title: String?,
    /** 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用 */
    val referrer: String?,
    /** 指定客服id */
    var staffId: Long?,
    /** 指定客服组id */
    var groupId: Long?,
    /** 访客选择多入口分流模版id */
    val shuntId: Long,
    /** 机器人优先开关（访客分配） */
    val robotShuntSwitch: Int?,
    /** vip等级 1-10 */
    val vipLevel: Int?,
    /** 客户来源类型 */
    val fromType: FromType,
    /** 客户IP */
    val ip: String,
    /** 登录时间 */
    var loginTime: Instant = Instant.now(),
    //是否在线
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /**
     * 上一条接受的消息ID，或者事件序列ID
     * 用以检查是否漏收了消息
     */
    var pts: Long? = null,
    // 客户待回复
    var needReply: Boolean = false,
    // 已经自动回复
    var autoReply: Boolean = false,
    // 最后待回复时间
    var lastReplyTime: Instant = Instant.now(),
) : java.io.Serializable {
    /** 客户所处服务器名称 */
    var clientAccessServerMap: MutableMap<String, String> = LinkedHashMap()

    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    fun setOffline(accessServer: String?) =
        apply {
            if (accessServer != null) {
                this.clientAccessServerMap.remove(accessServer)
                if (this.clientAccessServerMap.isEmpty()) {
                    this.onlineStatus = OnlineStatus.OFFLINE
                }
            } else {
                this.onlineStatus = OnlineStatus.OFFLINE
            }
        }

    fun statisticsByChatMessage(message: Message) = let {
        /**
         * 处理 记录消息产生的临时信息
         */
        if (message.creatorType == CreatorType.CUSTOMER) {
            if (!it.needReply) {
                // 设置需要客户回复
                it.needReply = true
                it.autoReply = false
                it.lastReplyTime = Instant.now()
            }
        } else if (message.creatorType == CreatorType.STAFF) {
            it.lastReplyTime = Instant.now()
            if (message.content.sysCode == SysCode.AUTO_REPLY) {
                // 自动回复消息
                it.autoReply = true
            } else {
                it.autoReply = false
                // 是客服消息，需要设置已回复状态
                it.needReply = false
            }
        }
        if (message.type == CreatorType.CUSTOMER) {
            it.pts = message.seqId
        }
    }

    fun setOnline() = apply { this.onlineStatus = OnlineStatus.ONLINE }
}