package com.planverse.server.reply.repository

import com.planverse.server.reply.entity.ReplyInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyInfoRepository : JpaRepository<ReplyInfoEntity, Long> {
    fun findAllByCommentInfoId(commentId: Long, pageable: Pageable): Slice<ReplyInfoEntity>
}