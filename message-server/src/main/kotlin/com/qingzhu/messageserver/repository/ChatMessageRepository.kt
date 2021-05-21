package com.qingzhu.messageserver.repository

import com.qingzhu.messageserver.domain.entity.ChatMessage
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

/**
 * 系统只负责写入ES，查询统计通过 kibana 进行
 */
interface ChatMessageRepository : CoroutineSortingRepository<ChatMessage, Long>