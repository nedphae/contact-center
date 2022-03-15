package com.qinghzu.graphqlbff.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qingzhu.common.security.awaitWithAuthentication
import com.qingzhu.common.util.RawStringSerializer
import org.springframework.beans.factory.annotation.Autowired

class CustomerSearchHit : SearchHit<Customer>()

class CustomerSearchHitPage : RestResponsePage<CustomerSearchHit>()

@GraphQLDescription("提供企业自定义的用户信息标识")
data class DetailData(
    @GraphQLDescription(
        """数据项的名称
用于区别不同的数据。其中real_name、mobile_phone、email为保留字段，
分别对应客服工作台用户信息中的“姓名”、“手机”、“邮箱”这三项数据。
保留关键字对应的数据项中，index、label属性将无效"""
    )
    val key: String,
    @GraphQLDescription("该数据显示的值，类型不做限定")
    val value: String,
    @GraphQLDescription("该项数据显示的名称")
    val label: String,
    @GraphQLDescription(
        """用于排序，显示数据时数据项按index值升序排列；
不设定index的数据项将排在后面；
index相同或未设定的数据项将按照其在 JSON 中出现的顺序排列。"""
    )
    val index: Int?,
    @GraphQLDescription(
        """超链接地址。若指定该值，
则该项数据将显示为超链接样式，点击后跳转到其值所指定的 URL 地址。"""
    )
    val href: String?,
    @GraphQLDescription(
        """仅对mobile_phone、email两个保留字段有效，
表示是否隐藏对应的数据项，true为隐藏，false为不隐藏。
若不指定，默认为false不隐藏。"""
    )
    val hidden: Boolean = false
)

class CustomerPage : RestResponsePage<Customer>()

@GraphQLDescription("客户信息")
data class Customer(
    @GraphQLDescription("用户id")
    val id: Long,
    @GraphQLDescription("公司id")
    val organizationId: Int,
    @GraphQLDescription(
        """用户id
用户在企业产品中的标识，便于后续客服系统中查看该用户在产品中的相关信息，
不传表示匿名用户 。若要指定用户信息，不显示默认的（guestxxx用户姓名），就必须传uid"""
    )
    val uid: String,
    @GraphQLDescription("用户姓名")
    var name: String?,
    @GraphQLDescription("用户邮箱")
    var email: String?,
    @GraphQLDescription("用户手机号")
    var mobile: String?,
    @GraphQLDescription("用户地址")
    var address: String?,
    /** 地理空间 */
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var location: String?,
    @GraphQLDescription("vip等级 1-10")
    var vipLevel: Int?,
    @GraphQLDescription("备注")
    var remarks: String?,
    @GraphQLDescription("批量获取的detailData")
    val data: List<DetailData>?,
) {
    @GraphQLDescription("客户在线状态，readOnly，批量时请不要查询此状态")
    suspend fun status(@GraphQLIgnore @Autowired messageService: MessageService, context: MyGraphQLContext) =
        messageService.findCustomerStatus(id).awaitWithAuthentication(context.oAuth)
}

@GraphQLDescription("客户在线状态")
data class CustomerStatus(
    @GraphQLDescription("公司id")
    val organizationId: Int,
    @GraphQLDescription("客户系统id")
    val userId: Long,
    @GraphQLDescription("客户提交id")
    val uid: String?,
    @GraphQLDescription("自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用")
    val title: String?,
    @GraphQLDescription("自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用")
    val referrer: String?,
    @GraphQLDescription("指定客服id")
    var staffId: Long?,
    @GraphQLDescription("指定客服组id")
    var groupId: Long?,
    @GraphQLDescription("访客选择多入口分流模版id")
    val shuntId: Long,
    @GraphQLDescription("机器人优先开关（访客分配）")
    val robotShuntSwitch: Int?,
    @GraphQLDescription("vip等级 1-10")
    val vipLevel: Int?,
    @GraphQLDescription("客户来源类型")
    val fromType: Int,
    @GraphQLDescription("客户IP")
    val ip: String,
    @GraphQLDescription("登录时间")
    var loginTime: Double,
    @GraphQLDescription("是否在线")
    var onlineStatus: Int,
    @GraphQLIgnore
    @GraphQLDescription(
        """上一条接受的消息ID，或者事件序列ID
用以检查是否漏收了消息"""
    )
    var pts: Long? = null,
    // 客户待回复
    var needReply: Boolean = false,
    // 已经自动回复
    var autoReply: Boolean = false,
    // 客户待回复
    var lastMsgTime: Double,
)