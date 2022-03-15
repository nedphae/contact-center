package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.broker.KafkaBroker
import com.qingzhu.imaccess.domain.constant.OnlineStatus
import com.qingzhu.imaccess.domain.query.StaffConfig

data class StaffChangeStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,

    val clientId: String?,
)

/**
 * 设置客服状态(初始状态)
 */
data class StaffStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: String,
    /** 所处接待组 */
    var shunt: List<Long>,
    /** 客服分组 **/
    var groupId: Long,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /** 最大接待数量 */
    var maxServiceCount: Int = 10,
    /** Which server i`m in
     * 如果需要配置登陆端互提，可将 A 更改为终端类型枚举
     */
    val clientAccessServer: Pair<String, String>,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    val staffType: Int = 1,
) {
    companion object {
        /**
         * [clientId] 为 socket io session id
         */
        fun fromStaffConfigAndStaff(
            staffConfig: StaffConfig,
            receptionistShuntDto: ReceptionistShuntDto,
            clientId: String
        ): StaffStatusDto {
            return StaffStatusDto(
                organizationId = staffConfig.organizationId!!,
                staffId = staffConfig.staffId!!,
                role = staffConfig.role!!,
                shunt = receptionistShuntDto.shunt,
                groupId = receptionistShuntDto.groupId,
                priorityOfShunt = receptionistShuntDto.priorityOfShunt,
                onlineStatus = staffConfig.onlineStatus,
                maxServiceCount = receptionistShuntDto.maxServiceCount,
                clientAccessServer = clientId to KafkaBroker.accessServer,
            )
        }
    }
}