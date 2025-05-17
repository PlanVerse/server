package com.planverse.server.comment.service

import com.planverse.server.comment.dto.CommentInfoDTO
import com.planverse.server.comment.dto.CommentInfoRequestDTO
import com.planverse.server.comment.dto.CommentInfoUpdateRequestDTO
import com.planverse.server.comment.repository.CommentInfoRepository
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import com.planverse.server.workflow.repository.WorkflowInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentInfoService(
    private val userInfoRepository: UserInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val workflowInfoRepository: WorkflowInfoRepository,
    private val commentInfoRepository: CommentInfoRepository,
) {
    fun getCommentList(userInfo: UserInfo, workflowInfoId: Long, pageable: Pageable): Slice<CommentInfoDTO> {
        return workflowInfoRepository.findById(workflowInfoId).orElseThrow {
            BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { workflowInfo ->
            projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(workflowInfo.projectInfoId, userInfo.id!!, Constant.DEL_N).takeIf { isMember ->
                !isMember
            }?.let {
                throw BaseException(StatusCode.NOT_PROJECT_MEMBER)
            }

            commentInfoRepository.findAllByWorkflowInfoIdAndDeleteYn(workflowInfoId, Constant.DEL_N, pageable).map { commentInfo ->
                val user = userInfoRepository.findById(commentInfo.createdBy!!).orElseThrow {
                    BaseException(StatusCode.USER_NOT_FOUND)
                }
                CommentInfoDTO.toDTO(commentInfo, user.name, user.email)
            }
        }
    }

    @Transactional
    fun createComment(userInfo: UserInfo, commentInfoRequestDTO: CommentInfoRequestDTO) {
        workflowInfoRepository.findById(commentInfoRequestDTO.workflowInfoId).orElseThrow {
            BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { workflowInfo ->
            projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(workflowInfo.projectInfoId, userInfo.id!!, Constant.DEL_N).takeIf { isMember ->
                !isMember
            }?.let {
                throw BaseException(StatusCode.NOT_PROJECT_MEMBER)
            }

            commentInfoRepository.save(commentInfoRequestDTO.toEntity())
        }
    }

    @Transactional
    fun modifyComment(userInfo: UserInfo, commentInfoUpdateRequestDTO: CommentInfoUpdateRequestDTO) {
        commentInfoRepository.findByIdAndCreatedBy(commentInfoUpdateRequestDTO.id, userInfo.id!!).orElseThrow {
            BaseException(StatusCode.COMMENT_NOT_FOUND)
        }.let {
            it.content = commentInfoUpdateRequestDTO.content

            commentInfoRepository.save(it)
        }
    }
}