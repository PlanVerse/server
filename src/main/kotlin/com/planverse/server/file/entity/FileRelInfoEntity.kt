package com.planverse.server.file.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "file_rel_info", schema = "public")
class FileRelInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Size(max = 255)
    @NotNull
    @Column(name = "target", nullable = false)
    var target: String? = null,

    @NotNull
    @Column(name = "target_id", nullable = false)
    var targetId: Long? = null,

    @NotNull
    @Column(name = "file_info_id", nullable = false)
    var fileInfoId: Long? = null,
) : BaseEntity()