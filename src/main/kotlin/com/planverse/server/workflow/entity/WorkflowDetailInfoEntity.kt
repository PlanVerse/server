package com.planverse.server.workflow.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "workflow_detail_info", schema = "public")
class WorkflowDetailInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "workflow_info_id", nullable = false)
    var workflowInfoId: Long,

    @NotNull
    @Column(name = "start_at", nullable = true)
    var startAt: LocalDateTime? = null,

    @NotNull
    @Column(name = "end_at", nullable = true)
    var endAt: LocalDateTime? = null,
) : BaseEntity()