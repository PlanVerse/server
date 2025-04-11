package com.planverse.server.reply.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "reply_info", schema = "public")
class ReplyInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Size(max = 255)
    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    var key: String? = null,

    @NotNull
    @Column(name = "comment_info_id", nullable = false)
    var commentInfoId: Long,

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false)
    var content: List<Map<String, Any>>? = emptyList()
) : BaseEntity()