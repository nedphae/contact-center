package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Customer
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CustomerDataLoaderQuery : Query {

    @GraphQLDescription("根据 机构id(oid), 和 用户id 获取用户信息")
    suspend fun getCustomer(@GraphQLIgnore @Autowired customerService: CustomerService, @GraphQLIgnore context: MyGraphQLContext, oid: Int, userId: Long): Customer? {
        return customerService.findCustomer(oid, userId).awaitWithAuthentication(context.oAuth)
    }

}