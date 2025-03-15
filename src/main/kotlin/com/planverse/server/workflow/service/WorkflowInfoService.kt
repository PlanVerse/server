package com.planverse.server.workflow.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.workflow.dto.WorkFlowInfoDTO
import com.planverse.server.workflow.dto.WorkFlowInfoRequestDTO
import com.planverse.server.workflow.repository.WorkflowInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkflowInfoService(
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val workflowInfoRepository: WorkflowInfoRepository,
) {
    fun getWorkflowList(userInfo: UserInfo, projectInfoId: Long): List<WorkFlowInfoDTO> {
        return workflowInfoRepository.findByProjectInfoId(projectInfoId).orElse(emptyList()).map { workflowInfo ->
            WorkFlowInfoDTO.toDto(workflowInfo)
        }
    }

    fun getWorkflowContent(userInfo: UserInfo, workflowInfoId: Long): WorkFlowInfoDTO {
        return workflowInfoRepository.findById(workflowInfoId).orElseThrow {
            throw BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { WorkFlowInfoDTO.toDto(it) }
    }

    fun createWorkflowContent(userInfo: UserInfo, workFlowInfoRequestDTO: WorkFlowInfoRequestDTO) {
        if (!projectInfoRepository.existsByIdAndDeleteYn(workFlowInfoRequestDTO.projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }

        if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(workFlowInfoRequestDTO.projectInfoId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N)) {
            throw BaseException(StatusCode.NOT_PROJECT_CREATOR)
        }

        var workflowInfoEntity = workFlowInfoRequestDTO.toEntity()
    }
}