package com.planverse.server.comment.entity

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
@Table(name = "comment_info", schema = "public")
class CommentInfoEntity(
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
    @Column(name = "workflow_info_id", nullable = false)
    var workflowInfoId: Long,

    @NotNull
    @Column(name = "content", nullable = false)
    var content: String? = null
) : BaseEntity()