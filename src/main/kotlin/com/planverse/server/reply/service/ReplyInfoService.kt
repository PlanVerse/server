package com.planverse.server.reply.service

import com.planverse.server.reply.repository.ReplyInfoRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReplyInfoService(
    private val replyInfoRepository: ReplyInfoRepository,
) {
    fun getReplyList(userInfo: UserInfo, commentId: Long, pageable: Pageable) {

    }
}