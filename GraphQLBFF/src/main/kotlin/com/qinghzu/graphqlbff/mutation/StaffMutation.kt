package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.*
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class StaffMutation(
    private val staffAdminService: StaffAdminService,
    private val messageService: MessageService,
    private val customerService: CustomerService,
    ): Mutation {
    @GraphQLDescription("保存客服信息")
    suspend fun saveStaff(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服信息") staff: Staff): Staff? {
        return staffAdminService.saveStaff(staff).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("保存客服分组")
    suspend fun saveStaffGroup(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服分组信息") staffGroup: StaffGroup): StaffGroup? {
        return staffAdminService.saveStaffGroup(staffGroup).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("保存客服接待组")
    suspend fun saveShunt(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服接待组信息") shunt: Shunt): Shunt? {
        return staffAdminService.saveShunt(shunt).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("保存客服接待组分类")
    suspend fun saveShuntClass(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服接待组信息") shuntClass: ShuntClass): ShuntClass? {
        return staffAdminService.saveShuntClass(shuntClass).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("更新客服在线状态")
    suspend fun updateStaffStatus(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服在线状态信息") updateStaffStatus: UpdateStaffStatus): StaffStatus? {
        return messageService.updateStaffStatus(updateStaffStatus).awaitWithAuthentication(context.oAuth)
    }
    @GraphQLDescription("请求分配客户")
    suspend fun assignmentFromQueue(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("客服在线状态信息") staffStatus: StaffStatus): StaffStatus {
        customerService.assignmentFromQueueForStaff(staffStatus).awaitWithAuthentication(context.oAuth)
        return staffStatus
    }
    @GraphQLDescription("批量删除客服")
    suspend fun deleteStaffByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long>?{
        staffAdminService.deleteStaffByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }
    @GraphQLDescription("批量删除客服组")
    suspend fun deleteStaffGroupByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long>?{
        staffAdminService.deleteGroupByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }
    @GraphQLDescription("批量删除客服接待组")
    suspend fun deleteShuntByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long>?{
        staffAdminService.deleteShuntByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }
}
