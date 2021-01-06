package com.qingzhu.staffadmin

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.staffadmin.staff.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.domain.entity.StaffGroup
import com.qingzhu.staffadmin.staff.repo.dao.StaffGroupRepository
import com.qingzhu.staffadmin.staff.repo.dao.StaffRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
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
    private lateinit var staffRepository: StaffRepository

    @Autowired
    private lateinit var staffGroupRepository: StaffGroupRepository

    /**
     * 插入预设数据
     */
    @Test
    fun testInsertStaff() {
        val staffGroup = StaffGroup(9491).also {
            it.groupName = "测试"
        }

        if (!staffGroupRepository.findDistinctTopByGroupName(staffGroup.groupName).isPresent) {
            staffGroupRepository.save(staffGroup)
        }

        val staff = Staff(
                organizationId = 9491,
                username = "admin",
                // 123456
                password = getBCryptPasswordEncoder().encode("123456"),
                role = StaffAuthority.ROLE_ADMIN,
                staffGroupId = staffGroup.id ?: 0
        )
        if (staffRepository.findFirstByOrganizationIdAndUsername(staff.organizationId, staff.username).isPresent.not()) {
            staffRepository.save(staff)
        }
    }

}
