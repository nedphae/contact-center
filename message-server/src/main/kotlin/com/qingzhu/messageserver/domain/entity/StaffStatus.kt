package com.qingzhu.messageserver.domain.entity

import com.hazelcast.nio.serialization.Portable
import com.hazelcast.nio.serialization.PortableFactory
import com.hazelcast.nio.serialization.PortableReader
import com.hazelcast.nio.serialization.PortableWriter
import com.qingzhu.common.constant.Default
import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap

class StaffStatusPortableFactory : PortableFactory {
    override fun create(classId: Int): Portable? {
        return if (StaffStatus.ID == classId) StaffStatus() else null
    }
}

data class StaffStatus(
    /** 公司id */
    var organizationId: Int,
    /** 客服id
     *
     * 每个客服只能保存一个状态 */
    var staffId: Long,
    /** 角色种类 */
    var role: StaffAuthority,
    /** 所处接待组 */
    var shunt: Set<Long>,
    /** 客服分组 **/
    var groupId: Long,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    var staffType: Int = 1,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /**
     * 上一条接受的消息ID，或者事件序列ID
     * 用以检查是否漏收了消息
     */
    var pts: Long?,
    /** 登录时间 */
    var loginTime: Instant = Instant.now(),
    /** 不同接待组的优先级 */
    var priorityOfShuntMap: Map<Long, Int> = LinkedHashMap(),
    /**
     * 服务的用户id
     */
    var userIdList: MutableSet<Long> = HashSet(),
    /** 客服所处服务器名称 */
    var clientAccessServerMap: MutableMap<String, String> = LinkedHashMap(),
    /** for copy only */
    private var _currentServiceCount: Int = 0,
    private var _autoBusy: Boolean = false,
) : Portable {

    constructor() : this(-1, -1, StaffAuthority.ROLE_STAFF, HashSet<Long>(), -1, -1, -1)

    @Default
    constructor(
        organizationId: Int,
        staffId: Long,
        role: StaffAuthority,
        shunt: Set<Long>,
        groupId: Long,
        maxServiceCount: Int,
        staffType: Int = 1,
        onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    ) : this(
        organizationId = organizationId,
        staffId = staffId,
        role = role,
        shunt = shunt,
        groupId = groupId,
        maxServiceCount = maxServiceCount,
        staffType = staffType,
        onlineStatus = onlineStatus,
        pts = null,
        loginTime = Instant.now(),
    )

    /** 当前接待量 (不能大于最大接待量) */
    var currentServiceCount: Int = _currentServiceCount
        set(value) {
            autoBusy = value >= maxServiceCount
            field = value
            _currentServiceCount = value
        }

    /** 自动忙碌(当当前接待量大于等于最大接待量时) */
    var autoBusy: Boolean = _autoBusy
        // 只能自动设置
        private set(value) {
            field = value
            _autoBusy = value
        }

    fun setOffline(clientId: String?) = apply {
        clientAccessServerMap.remove(clientId)
        if (clientAccessServerMap.isEmpty()) {
            onlineStatus = OnlineStatus.OFFLINE
        }
    }

    fun isOffLine() = onlineStatus == OnlineStatus.OFFLINE

    fun isBot() = staffType == 0

    companion object {
        const val ID = 1
        const val FactoryId = 1
    }

    override fun getFactoryId(): Int {
        return FactoryId
    }

    override fun getClassId(): Int {
        return ID
    }

    fun deepCopy(): StaffStatus = this.copy().also {
        it.priorityOfShuntMap = LinkedHashMap(this.priorityOfShuntMap)
        it.userIdList = HashSet(this.userIdList)
        it.clientAccessServerMap = LinkedHashMap(this.clientAccessServerMap)
    }

    /**
     * 为了使用 hazelcast map 的 replace, 在使用字节判断的时候 hashmap 的序列化字节会不同
     * 同时这种方式提高了一定的效率
     * [github hazelcast issues](https://github.com/hazelcast/hazelcast/issues/12574)
     */
    override fun writePortable(writer: PortableWriter) {
        writer.writeInt("organizationId", organizationId)
        writer.writeLong("staffId", staffId)
        writer.writeString("role", role.name)
        writer.writeLongArray("shunt", shunt.toLongArray())
        writer.writeLong("groupId", groupId)
        writer.writeInt("maxServiceCount", maxServiceCount)
        writer.writeInt("staffType", staffType)
        writer.writeString("onlineStatus", onlineStatus.name)
        writer.writeLong("pts", pts ?: -1)
        writer.writeTimestamp("loginTime", LocalDateTime.ofInstant(loginTime, ZoneId.systemDefault()))
        writer.writeString("priorityOfShuntMap", priorityOfShuntMap.toJson())
        writer.writeLongArray("userIdList", userIdList.toLongArray())
        writer.writeString("clientAccessServerMap", clientAccessServerMap.toJson())
        writer.writeInt("currentServiceCount", _currentServiceCount)
        writer.writeBoolean("autoBusy", _autoBusy)
    }

    override fun readPortable(reader: PortableReader) {
        organizationId = reader.readInt("organizationId")
        staffId = reader.readLong("staffId")
        role = reader.readString("role")?.let { StaffAuthority.valueOf(it) } ?: StaffAuthority.ROLE_STAFF
        shunt = reader.readLongArray("shunt")?.toSet() ?: HashSet()
        groupId = reader.readLong("groupId")
        maxServiceCount = reader.readInt("maxServiceCount")
        staffType = reader.readInt("staffType")
        onlineStatus = reader.readString("onlineStatus")?.let { OnlineStatus.valueOf(it) } ?: OnlineStatus.OFFLINE
        pts = reader.readLong("pts").let { if (it == -1L) null else it }
        loginTime = reader.readTimestamp("loginTime")?.atZone(ZoneId.systemDefault())?.toInstant() ?: Instant.now()
        userIdList = reader.readLongArray("userIdList")?.toMutableSet() ?: HashSet()
        currentServiceCount = reader.readInt("currentServiceCount")
        autoBusy = reader.readBoolean("autoBusy")
        priorityOfShuntMap = reader.readString("priorityOfShuntMap")?.let { JsonUtils.fromJson(it) } ?: LinkedHashMap()
        clientAccessServerMap =
            reader.readString("clientAccessServerMap")?.let { JsonUtils.fromJson(it) } ?: LinkedHashMap()
    }
}