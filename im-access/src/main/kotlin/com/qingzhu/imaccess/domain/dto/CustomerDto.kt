package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.value.DetailData

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerDto(
    /** 公司id */
    val organizationId: Int,
    /** 客户 id 服务器自动设置 */
    @JsonAlias("id")
    val userId: Long? = null,
    /**
     * 用户id
     * 用户在企业产品中的标识，便于后续客服系统中查看该用户在产品中的相关信息，
     * 不传表示匿名用户 。若要指定用户信息，不显示默认的（guestxxx用户姓名），就必须传uid
     */
    val uid: String,
    /** 用户姓名 */
    val name: String?,
    /** 用户邮箱 */
    val email: String?,
    /** 用户手机号 */
    val mobile: String?,
    /** vip等级 1-10 */
    val vipLevel: Int?,

    val data: List<DetailData>? = null,
) {
    companion object {
        fun fromCustomerConfig(customerConfig: CustomerConfig, shuntDto: ShuntDto): CustomerDto {
            return CustomerDto(
                organizationId = shuntDto.organizationId,
                uid = customerConfig.uid,
                name = customerConfig.name,
                email = customerConfig.email,
                mobile = customerConfig.mobile,
                vipLevel = customerConfig.vipLevel,
                data = customerConfig.data
            )
        }
    }
}