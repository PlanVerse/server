package com.planverse.server.step.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "step_detail_info")
class StepDetailInfoEntity(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "step_info_id", nullable = false)
    var stepInfoId: Long,

    @Size(max = 500)
    @Column(name = "step_name", length = 500, nullable = false)
    var stepName: String,

    @Column(name = "level", nullable = false)
    var level: Int,
) : BaseEntity()