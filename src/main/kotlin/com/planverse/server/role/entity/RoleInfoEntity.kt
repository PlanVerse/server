package com.planverse.server.role.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "role_info")
class RoleInfoEntity(
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Size(max = 255)
    @NotNull
    @Column(name = "role_name", nullable = false)
    var roleName: String,

    @Size(max = 500)
    @Column(name = "description", length = 500)
    var description: String? = null,
) : BaseEntity()