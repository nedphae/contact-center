package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl

class RestResponsePage<T> : SliceImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        @JsonProperty("content") content: List<T>?,
        @JsonProperty("number") number: Int,
        @JsonProperty("size") size: Int,
        @JsonProperty("totalElements") totalElements: Long?,
        @JsonProperty("pageable") pageable: JsonNode?,
        @JsonProperty("last") last: Boolean,
        @JsonProperty("totalPages") totalPages: Int,
        @JsonProperty("sort") sort: JsonNode?,
        @JsonProperty("first") first: Boolean,
        @JsonProperty("numberOfElements") numberOfElements: Int
    ) : super(
        content!!, PageRequest.of(number, size),
        !last
    )
    constructor(content: List<T>?, pageable: Pageable, last: Boolean) : super(content?:ArrayList<T>(), pageable, !last)
    constructor(content: List<T>?) : super(content!!)
    constructor() : super(ArrayList<T>())

    companion object {
        private const val serialVersionUID = 3248189030448292002L
    }
}