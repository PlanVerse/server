package com.planverse.server.comment.repository

import com.planverse.server.comment.entity.CommentInfoEntity
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CommentInfoRepository : JpaRepository<CommentInfoEntity, Long> {
    fun findByIdAndCreatedBy(id: Long, createdBy: Long): Optional<CommentInfoEntity>
    fun findAllByWorkflowInfoIdAndDeleteYn(workflowInfoId: Long, deleteYn: String, pageable: org.springframework.data.domain.Pageable): Slice<CommentInfoEntity>
}