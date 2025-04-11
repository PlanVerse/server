package com.planverse.server.comment.repository

import com.planverse.server.comment.entity.CommentInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface CommentInfoRepository : JpaRepository<CommentInfoEntity, Long> {
    fun findAllByTargetAndTargetId(target: String, targetId: Long, pageable: Pageable): Slice<CommentInfoEntity>
}