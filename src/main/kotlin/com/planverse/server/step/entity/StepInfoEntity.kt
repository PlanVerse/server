package com.planverse.server.step.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "step_info")
class StepInfoEntity(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @Size(max = 500)
    @Column(name = "title", length = 500, nullable = false)
    var title: String,
) : BaseEntity()