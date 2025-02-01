package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "workflow_info", schema = "public")
class WorkflowInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @NotNull
    @Column(name = "step_detail_info_id", nullable = false)
    var stepDetailInfoId: Long,

    @NotNull
    @Column(name = "no", nullable = false)
    var no: Long,

    @Size(max = 500)
    @NotNull
    @Column(name = "title", nullable = false, length = 500)
    var title: String,

    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    var content: String,
) : BaseEntity()