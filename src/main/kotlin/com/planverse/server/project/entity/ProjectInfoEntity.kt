package com.planverse.server.project.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "project_info", schema = "public")
class ProjectInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key", nullable = false)
    var key: String? = null,

    @Size(min = 3, max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    var name: String,

    @Size(max = 255)
    @Column(name = "description")
    var description: String? = null,
) : BaseEntity()