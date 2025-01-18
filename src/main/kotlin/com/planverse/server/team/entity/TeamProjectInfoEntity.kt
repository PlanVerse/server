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
@Table(name = "team_project_info", schema = "public")
class TeamProjectInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "team_member_info_id")
    var teamMemberInfoId: Long,

    @NotNull
    @Column(name = "project_info_id")
    var projectInfoId: Long,

    @NotNull
    @ColumnDefault("false")
    @Column(name = "creator")
    var creator: Boolean,
) : BaseEntity()