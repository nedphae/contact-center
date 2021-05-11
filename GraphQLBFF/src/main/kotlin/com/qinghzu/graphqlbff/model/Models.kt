package com.qinghzu.graphqlbff.model

import java.time.Instant

data class DetailData(
    /**
     * 数据项的名称
     * 用于区别不同的数据。其中real_name、mobile_phone、email为保留字段，
     * 分别对应客服工作台用户信息中的“姓名”、“手机”、“邮箱”这三项数据。
     * 保留关键字对应的数据项中，index、label属性将无效
     */
    val key: String,
    /** 该数据显示的值，类型不做限定 */
    val value: String,
    /** 该项数据显示的名称 */
    val label: String,
    /**
     * 用于排序，显示数据时数据项按index值升序排列；
     * 不设定index的数据项将排在后面；
     * index相同或未设定的数据项将按照其在 JSON 中出现的顺序排列。
     */
    val index: Int?,
    /**
     * 超链接地址。若指定该值，
     * 则该项数据将显示为超链接样式，点击后跳转到其值所指定的 URL 地址。
     */
    val href: String?,
    /**
     * 仅对mobile_phone、email两个保留字段有效，
     * 表示是否隐藏对应的数据项，true为隐藏，false为不隐藏。
     * 若不指定，默认为false不隐藏。
     */
    val hidden: Boolean = false
)

data class Customer(
    /** 公司id */
    val organizationId: Int,
    /** 客户系统id */
    val userId: Long,
    /** 客户提交id */
    val uid: String?,
    /** 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用 */
    val title: String?,
    /** 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用 */
    val referrer: String?,
    /** 指定客服id */
    var staffId: Long?,
    /** 指定客服组id */
    var groupId: Long?,
    /** 访客选择多入口分流模版id */
    val shuntId: Long,
    /** 机器人优先开关（访客分配） */
    val robotShuntSwitch: Int?,
    /** vip等级 1-10 */
    val vipLevel: Int?,
    /** 客户来源类型 */
    val fromType: Int,
    /** 客户IP */
    val ip: String,
    /** 客户所处服务器名称 */
    var clientAccessServerMap: MutableMap<String, String> = HashMap(),
    /** 登录时间 */
    var loginTime: Instant = Instant.now(),
    //是否在线
    var onlineStatus: Int = 1,
    /**
     * 上一条接受的消息ID，或者事件序列ID
     * 用以检查是否漏收了消息
     */
    var pts: Long? = null,

    /** 用户姓名 */
    var name: String?,
    /** 用户邮箱 */
    var email: String?,
    /** 用户手机号 */
    var mobile: String?,

    val data: List<DetailData>? = null,
)