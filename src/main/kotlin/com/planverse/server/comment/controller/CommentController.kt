package com.planverse.server.comment.controller

import com.planverse.server.comment.service.CommentService
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/comment")
class CommentController(
    private val commentService: CommentService
) {

    @GetMapping("/list/{workflowInfoId}")
    fun getCommentList(userInfo: UserInfo, @PathVariable(required = true) workflowInfoId: Long, pageable: Pageable) : BaseResponse<Any> {
        val res = commentService.getCommentList(userInfo, workflowInfoId, pageable)

        return BaseResponse.success(res)
    }
}