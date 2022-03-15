package com.qingzhu.bot.knowledgebase.repository.search

import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import kotlinx.coroutines.flow.Flow
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import reactor.core.publisher.Flux

interface TopicRepository : CoroutineSortingRepository<Topic, String> {
    fun findByKnowledgeBaseIdAndQuestion(knowledgeBaseId: Long, question: String): Flux<SearchHit<Topic>>
    fun findAllByOrganizationId(organizationId: Int): Flow<Topic>
}