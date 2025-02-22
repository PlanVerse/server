package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "project_member_info", schema = "public")
class ProjectMemberInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Column(name = "project_info_id", nullable = false)
    var projectInfoId: Long,

    @NotNull
    @Column(name = "team_info_id", nullable = false)
    var teamInfoId: Long,

    @NotNull
    @Column(name = "user_info_id", nullable = false)
    var userInfoId: Long,

    @NotNull
    @ColumnDefault("false")
    @Column(name = "creator", nullable = false)
    var creator: Boolean,
) : BaseEntity()