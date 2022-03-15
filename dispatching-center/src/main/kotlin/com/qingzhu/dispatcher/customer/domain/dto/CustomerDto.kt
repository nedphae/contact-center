package com.qingzhu.dispatcher.customer.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.message.getCustomerSnowFlake
import com.qingzhu.dispatcher.customer.domain.entity.Customer
import com.qingzhu.dispatcher.customer.domain.entity.DetailData
import org.springframework.data.elasticsearch.core.geo.GeoPoint

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerDto(
    val id: Long? = null,
    /** 公司id */
    val organizationId: Int?,
    /** 客户 id 服务器自动设置 */
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
    /** 用户地址 */
    val address: String?,
    /** 地理空间 */
    var location: GeoPoint?,
    /** vip等级 1-10 */
    val vipLevel: Int?,
    /** 备注 **/
    var remarks: String?,

    val data: List<DetailData>? = null,
) {
    companion object {
        fun fromCustomer(customer: Customer, data: List<DetailData>? = null): CustomerDto {
            return CustomerDto(
                id = customer.id,
                organizationId = customer.organizationId,
                uid = customer.uid,
                name = customer.name,
                email = customer.email,
                mobile = customer.mobile,
                address = customer.address,
                location = customer.location,
                vipLevel = customer.vipLevel,
                userId = customer.id,
                remarks = customer.remarks,
                data = data,
            )
        }
    }

    fun toCustomer(): Customer {

        return Customer(
            id = this.id ?: getCustomerSnowFlake().getNextSequenceId(),
            uid = this.uid,
            name = this.name,
            email = this.email,
            mobile = this.mobile,
            address = this.address,
            location = this.location,
            vipLevel = this.vipLevel,
            remarks = this.remarks,
            data = this.data
        ).also { it.organizationId = this.organizationId }
    }
}
