package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.CustomerBaseStatusDto
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.view.ConversationView
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class BotAccessService(
    private val registerService: RegisterService,
    private val dispatchingCenter: DispatchingCenter,
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
) {
    suspend fun register(customerConfig: CustomerConfig): ConversationView {
        val shuntDto = staffAdminService.getShuntByCode(customerConfig.shuntId).awaitSingleOrNull()
        return if (shuntDto == null) {
            ConversationView(errorCode = 404, errorMessage = "没有找到接待组")
        } else {
            // 检查 是否在缓存中 缓存10分钟
            var customer =
                messageService.findCustomerByUid(shuntDto.organizationId, customerConfig.uid).awaitSingleOrNull()
            // 如果缓存中还有状态，就不用再次注册了
            if (customer == null) {
                // 缓存中不存在客户信息就重新注册
                val customerStatusDto = registerService.registerCustomer(customerConfig, shuntDto).awaitSingle()
                customer = CustomerBaseStatusDto(shuntDto.organizationId, customerStatusDto.userId)
            }
            // 获取 客服预分配信息，bot http 转 websocket 如果转人工 然后分配失败就体验不好了
            // 所以使用 预先分配的客服
            dispatchingCenter.assignmentAuto(customer.organizationId, customer.userId).awaitSingle()
        }
    }


}