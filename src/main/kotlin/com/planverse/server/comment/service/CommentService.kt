package com.planverse.server.comment.service

import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(

) {
    fun getCommentList(userInfo: UserInfo, workflowInfoId: Long, pageable: Pageable) {

    }
}