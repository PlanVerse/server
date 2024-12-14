package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "assign_info")
class AssignInfoEntity(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "workflow_info_id", nullable = false)
    var workflowInfoId: Long,

    @NotNull
    @Column(name = "user_info_id", nullable = false)
    var userInfoId: Long,
) : BaseEntity()