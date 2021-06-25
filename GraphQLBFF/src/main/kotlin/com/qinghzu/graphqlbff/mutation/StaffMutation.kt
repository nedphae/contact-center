package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Shunt
import com.qinghzu.graphqlbff.model.Staff
import com.qinghzu.graphqlbff.model.StaffGroup
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class StaffMutation(private val staffAdminService: StaffAdminService): Mutation {
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
}
