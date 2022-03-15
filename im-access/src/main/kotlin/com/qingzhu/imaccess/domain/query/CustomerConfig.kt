package com.qingzhu.imaccess.domain.query

import com.qingzhu.common.constant.NoArg
import com.qingzhu.imaccess.domain.constant.FromType
import com.qingzhu.imaccess.domain.value.DetailData
import com.qingzhu.imaccess.util.getRandomInt

/**
 * 用户信息
 */
@NoArg
data class CustomerConfig(
    /** 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用 */
    val title: String?,
    /** 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用 */
    val referrer: String?,
    /**
     * 用户id
     * 用户在企业产品中的标识，便于后续客服系统中查看该用户在产品中的相关信息，
     * 不传表示匿名用户 。若要指定用户信息，不显示默认的（guestxxx用户姓名），就必须传uid
     */
    val uid: String = "guest_${getRandomInt()}",
    /** 访客选择多入口分流模版 uuid */
    val shuntId: String,
    /** 用户姓名 */
    val name: String? = null,
    /** 用户邮箱 */
    val email: String? = null,
    /** 用户手机号 */
    val mobile: String? = null,
    /** 企业当前登录用户其他信息，JSON字符串 */
    val data: List<DetailData>? = null,
    /** 指定客服id */
    val staffId: Long? = null,
    /** 指定客服组id */
    val groupId: Long? = null,
    /** 机器人优先开关（访客分配） */
    val robotShuntSwitch: Int? = null,
    /** vip等级 1-10 */
    val vipLevel: Int? = null,
    var fromType: FromType = FromType.WEB,
) {
    lateinit var ip: String
}