package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.qingzhu.common.util.RawStringSerializer

open class RestResponsePage<T>(
    @JsonProperty("content") var content: List<T>? = null,
    @JsonProperty("number") var number: Int = 0,
    @JsonProperty("size") var size: Int = 0,
    @JsonProperty("totalElements") var totalElements: Long = 0,
    @JsonProperty("pageable") var pageable: Pageable? = null,
    @JsonProperty("last") var last: Boolean = true,
    @JsonProperty("totalPages") var totalPages: Int = 0,
    @JsonProperty("sort") var sort: Sort? = null,
    @JsonProperty("first") var first: Boolean = true,
    @JsonProperty("numberOfElements") var numberOfElements: Int = 0,
    @JsonProperty("empty") var empty: Boolean = true,
)

data class Sort(
    val unsorted: Boolean,
    val sorted: Boolean,
    val empty: Boolean,
)

data class Pageable(
    val sort: Sort,
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean,
)

open class SearchHit<T>(
    var index: String? = null,
    var id: String? = null,
    var score: Float? = null,
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var sortValues: String? = null,
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var highlightFields: String? = null,
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var innerHits: String? = null,
    @field:JsonDeserialize(using = RawStringSerializer::class)
    var nestedMetaData: String? = null,
    var content: T? = null,
)