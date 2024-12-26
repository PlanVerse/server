package com.planverse.server.team.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "team_info", schema = "public")
class TeamInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    val name: String,

    @Size(max = 255)
    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    val key: String,

    @Size(max = 255)
    @Column(name = "description")
    val description: String? = null,
) : BaseEntity()