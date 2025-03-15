package com.planverse.server.workflow.service

import com.planverse.server.assign.dto.AssignInfoDTO
import com.planverse.server.assign.entity.AssignInfoEntity
import com.planverse.server.assign.repository.AssignInfoRepository
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.step.repository.StepInfoRepository
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
    private val stepInfoRepository: StepInfoRepository,
    private val assignInfoRepository: AssignInfoRepository,
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
        val projectInfoId = workFlowInfoRequestDTO.projectInfoId

        // 프로젝트 존재 여부 판단
        if (!projectInfoRepository.existsByIdAndDeleteYn(projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }

        // 프로젝트 생성자 여부 판단
        if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N)) {
            throw BaseException(StatusCode.NOT_PROJECT_CREATOR)
        }

        // 할당대상이 프로젝트에 소속된 멤버인지 판단
        workFlowInfoRequestDTO.assign?.map {
            if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(projectInfoId, it, Constant.DEL_N)) {
                throw BaseException(StatusCode.PROJECT_MEMBER_NOT_FOUND)
            }
        } ?: throw BaseException(StatusCode.REQUIRED_PARAMETER_IS_NULL)

        // step_info 정보 존재 여부 판단
        if (!stepInfoRepository.existsByIdAndProjectInfoIdAndDeleteYn(workFlowInfoRequestDTO.stepInfoId, projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
        }

        val workflowInfoEntity = workFlowInfoRequestDTO.toEntity()
        workflowInfoRepository.save(workflowInfoEntity)

        workFlowInfoRequestDTO.assign!!.map {
            assignInfoRepository.save(AssignInfoDTO.toEntity(workflowInfoEntity.id!!, it))
        }
    }
}