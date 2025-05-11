package com.planverse.server.comment.repository

import com.planverse.server.comment.entity.CommentInfoEntity
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface CommentInfoRepository : JpaRepository<CommentInfoEntity, Long> {
    fun findAllByWorkflowInfoIdAndDeleteYn(workflowInfoId: Long, deleteYn: String, pageable: org.springframework.data.domain.Pageable): Slice<CommentInfoEntity>
}