package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

class RestResponsePage<T>(
    @JsonProperty("content") val content: List<T>?,
    @JsonProperty("number") val number: Int = 0,
    @JsonProperty("size") val size: Int = 0,
    @JsonProperty("totalElements") val totalElements: Long = 0,
    @JsonProperty("pageable") val pageable: JsonNode?,
    @JsonProperty("last") val last: Boolean = true,
    @JsonProperty("totalPages") val totalPages: Int = 0,
    @JsonProperty("sort") val sort: JsonNode,
    @JsonProperty("first") val first: Boolean= true,
    @JsonProperty("numberOfElements") val numberOfElements: Int = 0,
    @JsonProperty("numberOfElements") val empty: Boolean,
)

data class SearchHit<T>(
    val index: String?,
    val id: String?,
    val score: Float,
    val sortValues: List<Any>,
    val highlightFields: Map<String, List<String>>?,
    val innerHits: Map<String?, JsonNode?>?,
    val nestedMetaData: JsonNode?,
    val content: T,
)