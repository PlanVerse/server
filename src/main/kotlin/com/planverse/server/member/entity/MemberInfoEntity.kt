package com.planverse.server.member.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "member_info")
class MemberInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "team_id", nullable = false)
    var teamId: Long? = null,

    @NotNull
    @Column(name = "user_id", nullable = false)
    var userId: Long? = null,
) : BaseEntity()