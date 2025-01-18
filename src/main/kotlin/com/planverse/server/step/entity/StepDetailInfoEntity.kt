package com.planverse.server.step.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "step_detail_info", schema = "public")
class StepDetailInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "step_info_id")
    var stepInfoId: Long,

    @Size(max = 500)
    @Column(name = "step_name", length = 500)
    var stepName: String,

    @Column(name = "level")
    var level: Int,
) : BaseEntity()