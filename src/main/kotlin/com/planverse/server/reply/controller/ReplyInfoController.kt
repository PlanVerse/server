package com.planverse.server.reply.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.reply.service.ReplyInfoService
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reply")
class ReplyInfoController(
    private val replyInfoService: ReplyInfoService
) {

    @GetMapping("/list/{commentId}")
    fun getReplyList(userInfo: UserInfo, @PathVariable(required = true) commentId: Long, pageable: Pageable): BaseResponse<Any> {
        val res = replyInfoService.getReplyList(userInfo, commentId, pageable)

        return BaseResponse.success(res)
    }
}