package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "project_info", schema = "public")
class ProjectInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    val key: String,

    @Size(min = 3, max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    val name: String,

    @Size(max = 255)
    @Column(name = "description")
    val description: String? = null,

    @Size(max = 500)
    @Column(name = "project_logo_url", length = 500)
    val projectLogoUrl: String? = null,
) : BaseEntity()