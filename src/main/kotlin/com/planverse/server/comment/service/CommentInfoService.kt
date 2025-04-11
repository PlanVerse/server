package com.planverse.server.comment.service

import com.planverse.server.comment.repository.CommentInfoRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentInfoService(
    private val commentInfoRepository: CommentInfoRepository,
) {
    fun getCommentList(userInfo: UserInfo, targetId: Long, pageable: Pageable) {
    }
}