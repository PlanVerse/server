package com.planverse.server.team.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "team_member_info", schema = "public")
class TeamMemberInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "user_info_id")
    var userInfoId: Long,

    @NotNull
    @Column(name = "team_info_id")
    var teamInfoId: Long,

    @NotNull
    @ColumnDefault("false")
    @Column(name = "creator")
    var creator: Boolean,
) : BaseEntity()