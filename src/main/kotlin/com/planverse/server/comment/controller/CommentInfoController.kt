package com.planverse.server.comment.controller

import com.planverse.server.comment.dto.CommentInfoRequestDTO
import com.planverse.server.comment.dto.CommentInfoUpdateRequestDTO
import com.planverse.server.comment.service.CommentInfoService
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comment")
class CommentInfoController(
    private val commentInfoService: CommentInfoService
) {

    @GetMapping("/list/{workflowInfoId}")
    fun getCommentList(userInfo: UserInfo, @PathVariable(required = true) workflowInfoId: Long, pageable: Pageable): BaseResponse<Any> {
        val res = commentInfoService.getCommentList(userInfo, workflowInfoId, pageable)

        return BaseResponse.success(res)
    }

    @PostMapping
    fun createComment(userInfo: UserInfo, @RequestBody commentInfoRequestDTO: CommentInfoRequestDTO): BaseResponse<Any> {
        val res = commentInfoService.createComment(userInfo, commentInfoRequestDTO)

        return BaseResponse.success(res)
    }

    @PutMapping
    fun modifyComment(userInfo: UserInfo, @RequestBody commentInfoUpdateRequestDTO: CommentInfoUpdateRequestDTO): BaseResponse<Any> {
        val res = commentInfoService.modifyComment(userInfo, commentInfoUpdateRequestDTO)

        return BaseResponse.success(res)
    }
}