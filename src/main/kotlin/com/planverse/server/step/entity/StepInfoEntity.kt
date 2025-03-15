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
@Table(name = "step_info", schema = "public")
class StepInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = false)
    var name: String,

    @Size(min = 1, max = 10000)
    @Column(name = "sort", nullable = false)
    var sort: Int,
) : BaseEntity()