package com.qingzhu.staffadmin

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.staffadmin.staff.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffGroupRepository
import com.qingzhu.staffadmin.staff.repository.ReactiveStaffRepository
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

    /**
     * 插入预设数据
     */
    @Test
    fun testInsertStaffR() {
        val staffGroup = StaffGroup(9491).also {
            it.groupName = "测试"
        }
        val groupSave = staffGroupRepositoryR.findDistinctTopByGroupName(staffGroup.groupName)
                .switchIfEmpty(staffGroupRepositoryR.save(staffGroup))

        StepVerifier.create(groupSave)
                .expectNext(staffGroup)
                .expectErrorMessage("boom")
                .verify()

        val staff = Staff(
                organizationId = 9491,
                username = "admin",
                // 123456
                password = getBCryptPasswordEncoder().encode("123456"),
                role = StaffAuthority.ROLE_ADMIN,
                staffGroupId = staffGroup.id ?: 0
        )

        staff.realName = "新之助"
        staff.nickName = "蜡笔小新"
        val staffSave = staffRepositoryR.findFirstByOrganizationIdAndUsername(staff.organizationId, staff.username)
                .switchIfEmpty(staffRepositoryR.save(staff))

        StepVerifier.create(staffSave)
                .expectNext(staff)
                .expectErrorMessage("boom")
                .verify()
    }

}
