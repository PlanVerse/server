package com.planverse.server.team.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "team_member_info")
class TeamMemberInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "user_info_id", nullable = false)
    var userInfoId: Long,

    @NotNull
    @Column(name = "team_info_id", nullable = false)
    var teamInfoId: Long,

    @NotNull
    @Column(name = "role_info_id", nullable = false)
    var roleInfoId: Long,
) : BaseEntity()