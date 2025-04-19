package com.planverse.server.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@SuperBuilder
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseHistoryEntity {
    @Column(name = "delete_yn", nullable = false)
    var deleteYn: String = "N"

    @Column(name = "created_by", nullable = false, updatable = false)
    var createdBy: Long? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_By", nullable = false)
    var updatedBy: Long? = null

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "changed_at", nullable = false)
    var changedAt: LocalDateTime? = null
}