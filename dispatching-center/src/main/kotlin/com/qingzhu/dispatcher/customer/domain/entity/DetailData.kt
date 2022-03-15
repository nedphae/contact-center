package com.qingzhu.dispatcher.customer.domain.entity

data class DetailData(
    /**
     * 数据项的名称
     * 用于区别不同的数据。其中real_name、mobile_phone、email为保留字段，
     * 分别对应客服工作台用户信息中的“姓名”、“手机”、“邮箱”这三项数据。
     * 保留关键字对应的数据项中，index、label属性将无效
     */
    val key: String,
    /** 该数据显示的值，类型不做限定 */
    var value: String,
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
