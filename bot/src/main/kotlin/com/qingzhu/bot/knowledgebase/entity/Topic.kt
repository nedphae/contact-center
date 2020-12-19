package com.qingzhu.bot.knowledgebase.entity

import org.springframework.data.elasticsearch.annotations.Document
import java.util.*

/**
 * 知识库主题 vo
 */
@Document(indexName = "uk_xiaoe_topic", type = "uckefu")
data class Topic(
        val id: String = UUID.randomUUID().toString().replace("-", ""),
        val sessionid: String,
        // 标题
        var title: String,
        // 内容
        var content: String,
        // 微信渠道回复
        var weixin: String,
        // 邮件渠道回复
        var email: String,
        // 短信回复
        var sms: String,
        // 语音播报回复
        var tts: String,
        // 问题价格
        var price: Float,
        // 关键词
        var keyword: String,
        // 摘要
        var summary: String,
        // 是否匿名提问
        var anonymous: Boolean,
        // 有效期开始
        var begintime: Date,
        // 有效期结束
        var endtime: Date,
        // 是否置顶
        var top: Boolean,
        // 是否精华
        var essence: Boolean,
        // 是否已采纳最佳答案
        var accept: Boolean,
        // 结贴
        var finish: Boolean,
        // 相似问题
        var silimar: List<String>
)