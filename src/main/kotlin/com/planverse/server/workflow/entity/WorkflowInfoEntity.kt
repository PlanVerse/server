package com.planverse.server.workflow.entity

import com.planverse.server.common.entity.BaseEntity
import com.planverse.server.common.listner.WorkflowInfoListener
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
@EntityListeners(WorkflowInfoListener::class)
@Table(name = "workflow_info", schema = "public")
class WorkflowInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Size(max = 255)
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    var key: String? = null,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @NotNull
    @Column(name = "step_info_id", nullable = false)
    var stepInfoId: Long,

    @Size(max = 500)
    @NotNull
    @Column(name = "title", nullable = false, length = 500)
    var title: String,

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false)
    var content: Map<String, Any>? = emptyMap()
) : BaseEntity()