package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.*
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("客服信息查询")
@Component
class StaffQuery(private val staffAdminService: StaffAdminService) : Query {
    @GraphQLDescription("查询客服组")
    suspend fun allStaffGroup(@GraphQLIgnore context: MyGraphQLContext): List<StaffGroup> {
        return staffAdminService.findAllGroup().awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("查询客服接待组分类")
    suspend fun allShuntClass(@GraphQLIgnore context: MyGraphQLContext): List<ShuntClass> {
        return staffAdminService.findAllShuntClass().awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("查询客服接待组")
    suspend fun allStaffShunt(@GraphQLIgnore context: MyGraphQLContext): List<Shunt> {
        return staffAdminService.findAllShunt().awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("查询客服列表")
    suspend fun allStaff(@GraphQLIgnore context: MyGraphQLContext): List<Staff> {
        return staffAdminService.findAllStaff().awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("根据id查询客服")
    suspend fun getStaffById(@GraphQLIgnore context: MyGraphQLContext, staffId: Long): Staff? {
        return staffAdminService.findStaffById(staffId).awaitWithAuthentication(context.oAuth)
    }
}