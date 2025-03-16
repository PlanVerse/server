package com.planverse.server.workflow.service

import com.planverse.server.assign.dto.AssignInfoDTO
import com.planverse.server.assign.dto.AssignInfoResponseDTO
import com.planverse.server.assign.repository.AssignInfoRepository
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.step.dto.StepInfoDTO
import com.planverse.server.step.repository.StepInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import com.planverse.server.workflow.dto.WorkFlowInfoRequestDTO
import com.planverse.server.workflow.dto.WorkFlowInfoResponseDTO
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import com.planverse.server.workflow.repository.WorkflowInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkflowInfoService(
    private val userInfoRepository: UserInfoRepository,
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val workflowInfoRepository: WorkflowInfoRepository,
    private val stepInfoRepository: StepInfoRepository,
    private val assignInfoRepository: AssignInfoRepository,
) {
    private fun getWorkFLowInfo(workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoResponseDTO {
        return WorkFlowInfoResponseDTO.toDto(workflowInfoEntity).also { workflow ->
            stepInfoRepository.findById(workflowInfoEntity.stepInfoId).orElseThrow {
                throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
            }.let { stepInfo ->
                workflow.stepInfo = StepInfoDTO.toDto(stepInfo)
            }

            workflow.assignInfo = buildList {
                assignInfoRepository.findAllByWorkflowInfoId(workflow.id!!).ifPresent { assignInfos ->
                    assignInfos.map { assignInfo ->
                        userInfoRepository.findById(assignInfo.userInfoId).orElseThrow {
                            throw BaseException(StatusCode.USER_NOT_FOUND)
                        }.let { userInfo ->
                            add(AssignInfoResponseDTO.toDto(assignInfo, userInfo.name, userInfo.email))
                        }
                    }
                }
            }
        }
    }

    private fun getWorkFLowInfo(userInfo: UserInfo, workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoResponseDTO {
        return this.getWorkFLowInfo(workflowInfoEntity).also {
            if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(workflowInfoEntity.projectInfoId, userInfo.id!!, Constant.DEL_N)) {
                throw BaseException(StatusCode.NOT_PROJECT_MEMBER)
            }
        }
    }

    fun getWorkflowList(userInfo: UserInfo, projectInfoId: Long): List<WorkFlowInfoResponseDTO> {
        return workflowInfoRepository.findByProjectInfoId(projectInfoId).orElse(emptyList()).map { workflowInfo ->
            getWorkFLowInfo(workflowInfo)
        }
    }

    fun getWorkflowContent(userInfo: UserInfo, workflowInfoId: Long): WorkFlowInfoResponseDTO {
        return workflowInfoRepository.findById(workflowInfoId).orElseThrow {
            throw BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { getWorkFLowInfo(userInfo, it) }
    }

    @Transactional
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
        workFlowInfoRequestDTO.assignInfo?.map {
            if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(projectInfoId, it, Constant.DEL_N)) {
                throw BaseException(StatusCode.PROJECT_MEMBER_NOT_FOUND)
            }
        }

        // step_info 정보 존재 여부 판단
        if (!stepInfoRepository.existsByIdAndProjectInfoIdAndDeleteYn(workFlowInfoRequestDTO.stepInfoId, projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
        }

        val workflowInfoEntity = workFlowInfoRequestDTO.toEntity()
        workflowInfoRepository.save(workflowInfoEntity)

        workFlowInfoRequestDTO.assignInfo!!.map {
            assignInfoRepository.save(AssignInfoDTO.toEntity(workflowInfoEntity.id!!, it))
        }
    }
}