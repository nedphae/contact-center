package com.qingzhu.dispatcher.customer.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.*
import org.springframework.data.elasticsearch.annotations.*
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

// @Entity
// @Table(uniqueConstraints = [UniqueConstraint(columnNames = ["organizationId", "uid"])],
//         indexes = [Index(columnList = "organizationId"),
//             Index(columnList = "uid"), Index(columnList = "mobile")])
@Document(indexName = "customer", shards = 1, replicas = 0)
data class Customer(
    @Id
    var id: Long? = null,
    /**
     * 用户id
     * 用户在企业产品中的标识，便于后续客服系统中查看该用户在产品中的相关信息，
     * 不传表示匿名用户 。若要指定用户信息，不显示默认的（guestxxx用户姓名），就必须传uid
     */
    @Field(type = FieldType.Keyword)
    val uid: String,
    /** 用户姓名 */
    @Field(type = FieldType.Text)
    var name: String?,
    /** 用户邮箱 */
    @Field(type = FieldType.Text)
    var email: String?,
    /** 用户手机号 */
    @Field(type = FieldType.Keyword)
    var mobile: String?,
    /** 用户地址 */
    @Field(type = FieldType.Text)
    var address: String?,
    /** 用户地理空间地址 */
    @GeoPointField
    var location: GeoPoint?,
    /** vip等级 1-10 */
    var vipLevel: Int?,
    /** 备注 **/
    @Field(type = FieldType.Text)
    var remarks: String?,

    @Field(type = FieldType.Object)
    val data: List<DetailData>? = null,

    @Field(type = FieldType.Object)
    val tags: List<String>? = null,

    @CreatedBy
    @JsonIgnore
    var createdBy: String? = null,

    @CreatedDate
    @JsonIgnore
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var createdDate: Instant = Instant.now(),

    @LastModifiedBy
    @JsonIgnore
    var lastModifiedBy: String? = null,

    @LastModifiedDate
    @JsonIgnore
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var lastModifiedDate: Instant = Instant.now(),

    var organizationId: Int? = null,
)