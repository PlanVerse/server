package com.planverse.server.comment.controller

import com.planverse.server.comment.service.CommentInfoService
import com.planverse.server.common.controller.SubscribeController
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/comment")
class CommentInfoController(
    private val commentInfoService: CommentInfoService
): SubscribeController {

    @GetMapping("/list/{targetId}")
    fun getCommentList(userInfo: UserInfo, @PathVariable(required = true) targetId: Long, pageable: Pageable): BaseResponse<Any> {
        val res = commentInfoService.getCommentList(userInfo, targetId, pageable)

        return BaseResponse.success(res)
    }

    override fun subscribe(userId: Long): SseEmitter {
        return subscribe(userId)
    }
}