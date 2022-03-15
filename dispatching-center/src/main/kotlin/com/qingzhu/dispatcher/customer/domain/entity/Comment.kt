package com.qingzhu.dispatcher.customer.domain.entity

import com.qingzhu.dispatcher.customer.domain.constant.CommentSolved
import com.qingzhu.dispatcher.customer.domain.constant.CommentSolvedWay
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.*
import java.time.Instant

/**
 * 用户留言
 */
@Table("customer_comment")
data class Comment(
    @PrimaryKeyColumn(name = "organization_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var organizationId: Int?,
    /** 留言时间 */
    @PrimaryKeyColumn(
        name = "created_at",
        ordinal = 1,
        type = PrimaryKeyType.CLUSTERED,
        ordering = Ordering.DESCENDING
    )
    val createdAt: Instant = Instant.now(),
    /** 留言时间 */
    @PrimaryKeyColumn(name = "shunt_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    /** 所属接待组 */
    val shuntId: Long,
    @Indexed
    val userId: Long,
    @Indexed
    val uid: String,
    val name: String,
    val mobile: String?,
    val email: String?,
    val message: String,
    @Indexed
    @CassandraType(type = CassandraType.Name.INT)
    val solved: CommentSolved = CommentSolved.UNSOLVED,
    @Indexed
    @CassandraType(type = CassandraType.Name.INT)
    @Column("solved_way")
    val solvedWay: CommentSolvedWay? = null,
    val fromPage: String? = null,
    val fromIp: String? = null,
    @CassandraType(
        type = CassandraType.Name.MAP,
        typeArguments = [CassandraType.Name.VARCHAR, CassandraType.Name.DOUBLE]
    )
    val geo: Map<String, Double>? = null,
    // 负责客服
    val responsible: Long? = null,
)
