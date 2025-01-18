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
    @Column(name = "project_info_id")
    var projectInfoId: Long,

    @NotNull
    @Column(name = "step_detail_info_id")
    var stepDetailInfoId: Long,

    @NotNull
    @Column(name = "no")
    var no: Long,

    @Size(max = 500)
    @NotNull
    @Column(name = "title", length = 500)
    var title: String,

    @NotNull
    @Column(name = "content", length = Integer.MAX_VALUE)
    var content: String,
) : BaseEntity()