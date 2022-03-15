package com.qingzhu.staffadmin

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffGroupRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class StaffAdminApplicationTests {
    companion object {
        init {
            // 修复 Redis ES 集成时的 netty 错误
            // 当前只有测试 报错
            System.setProperty("es.set.netty.runtime.available.processors", "false")
        }

    }

    @Test
    fun contextLoads() {
    }

    @Autowired
    private lateinit var staffRepositoryR: ReactiveStaffRepository

    @Autowired
    private lateinit var staffGroupRepositoryR: ReactiveStaffGroupRepository

    @Test
    fun testUpdate() {
        val update = staffGroupRepositoryR.findById(1)
            .doOnNext { it.groupName = "客服组" }
            .flatMap(this.staffGroupRepositoryR::save)
        StepVerifier.create(update)
            .consumeNextWith { assertEquals("客服组", it.groupName) }
            .verifyComplete()
    }

    /**
     * 插入预设数据
     */
    @Test
    fun testInsertStaff() {
        val staffGroup = StaffGroup(null, groupName = "客服组").also { it.organizationId = 9491 }
        val groupSave = staffGroupRepositoryR.findDistinctTopByGroupName(staffGroup.groupName)
            .switchIfEmpty(staffGroupRepositoryR.save(staffGroup))

        StepVerifier.create(groupSave)
            .consumeNextWith {
                it.groupName === "客服组"
            }
            .verifyComplete()

        val staff = Staff(
            username = "admin",
            // 123456
            password = getBCryptPasswordEncoder().encode("123456"),
            role = StaffAuthority.ROLE_ADMIN,
            staffGroupId = staffGroup.id ?: 0,
            realName = "新之助",
            nickName = "蜡笔小新",
            avatar = null
        ).also { it.organizationId = 9491 }
        val staffSave = staffRepositoryR.findFirstByOrganizationIdAndUsernameAndStaffTypeAndEnabled(
            staff.organizationId!!,
            staff.username,
            1,
            true
        )
            .switchIfEmpty(staffRepositoryR.save(staff))

        StepVerifier.create(staffSave)
            .consumeNextWith {
                it.username === "admin"
            }
            .verifyComplete()
    }

}
