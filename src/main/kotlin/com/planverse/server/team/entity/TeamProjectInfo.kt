package com.planverse.server.team.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "team_project_info")
class TeamProjectInfo(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "team_member_info_id", nullable = false)
    var teamMemberInfoId: Long,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @NotNull
    @Column(name = "role_info_id", nullable = false)
    var roleInfoId: Long,
) : BaseEntity()