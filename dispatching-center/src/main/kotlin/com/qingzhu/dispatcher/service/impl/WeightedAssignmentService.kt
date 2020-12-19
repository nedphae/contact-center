package com.qingzhu.dispatcher.service.impl

import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import com.qingzhu.dispatcher.service.MessageService
import com.qingzhu.dispatcher.service.AssignmentInterface
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.SecureRandom
import kotlin.collections.ArrayList

/**
 * 随机加权平均分配
 * 效率高，但是达不到绝对平均
 */
@Service
class WeightedAssignmentService(
        private val messageService: MessageService
) : AssignmentInterface {
    override fun assignmentStaff(organizationId: Int, shuntId: Long): Mono<Long> {
        val staffDispatcherDtoList = messageService.findIdleStaff(organizationId, shuntId)
        return if (staffDispatcherDtoList.isNullOrEmpty()) {
            Mono.empty()
        } else {
            val solution = Solution(shuntId, staffDispatcherDtoList)
            Mono.justOrEmpty(staffDispatcherDtoList[solution.pickIndex()].staffId)
        }
    }

    private class Solution(shuntId: Long, staffDispatcherDtoList: List<StaffDispatcherDto>) {
        companion object {
            private val rand = SecureRandom()
        }

        private val sum = ArrayList<Int>()
        private var tot = 0

        init {
            staffDispatcherDtoList.forEach {
                tot += it.priorityOfGroup[shuntId] ?: 0
                sum.add(tot)
            }
        }

        fun pickIndex(): Int {
            val tag = rand.nextInt(tot)

            var low = 0
            var high = sum.size - 1
            // 查询满足 tag < p[x] 的最小下标。
            while (low != high) {
                val mid = (low + high) / 2
                // 二分查找
                if (tag >= sum[mid]) low = mid + 1
                else high = mid
            }
            return low
        }
    }
}