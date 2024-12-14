package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "project_info")
class ProjectInfoEntity(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @NotNull
    @Column(name = "no", nullable = false)
    var no: Long,

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    var name: String,

    @Size(max = 255)
    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    var key: String,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,

    @Size(max = 500)
    @Column(name = "project_logo_url", length = 500)
    var projectLogoUrl: String? = null,
) : BaseEntity()