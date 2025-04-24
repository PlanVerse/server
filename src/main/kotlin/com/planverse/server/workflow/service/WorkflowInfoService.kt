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
import com.planverse.server.workflow.dto.WorkFlowDetailInfoDTO
import com.planverse.server.workflow.dto.WorkFlowInfoRequestDTO
import com.planverse.server.workflow.dto.WorkFlowInfoResponseDTO
import com.planverse.server.workflow.dto.WorkFlowInfoUpdateRequestDTO
import com.planverse.server.workflow.entity.WorkflowDetailInfoEntity
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import com.planverse.server.workflow.repository.WorkflowDetailInfoRepository
import com.planverse.server.workflow.repository.WorkflowInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkflowInfoService(
    private val userInfoRepository: UserInfoRepository,
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val workflowInfoRepository: WorkflowInfoRepository,
    private val workflowDetailInfoRepository: WorkflowDetailInfoRepository,
    private val stepInfoRepository: StepInfoRepository,
    private val assignInfoRepository: AssignInfoRepository,
) {
    private fun getWorkFLowInfo(workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoResponseDTO {
        return WorkFlowInfoResponseDTO.toDto(workflowInfoEntity).also { workflowInfo ->
            val workflowInfoId = workflowInfo.id ?: throw BaseException(StatusCode.WORKFLOW_NOT_FOUND)

            workflowDetailInfoRepository.findByWorkflowInfoId(workflowInfoId).orElseThrow {
                throw BaseException(StatusCode.WORKFLOW_DETAIL_NOT_FOUND)
            }.let { detailInfo ->
                workflowInfo.detailInfo = WorkFlowDetailInfoDTO.toDto(detailInfo)
            }

            stepInfoRepository.findById(workflowInfoEntity.stepInfoId).orElseThrow {
                throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
            }.let { stepInfo ->
                workflowInfo.stepInfo = StepInfoDTO.toDto(stepInfo)
            }

            workflowInfo.assignInfo = buildList {
                assignInfoRepository.findAllByWorkflowInfoId(workflowInfoId).orElse(emptyList()).let { assignInfos ->
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

    fun getWorkflowList(userInfo: UserInfo, projectInfoId: Long, pageable: Pageable): Slice<WorkFlowInfoResponseDTO> {
        projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(projectInfoId, userInfo.id!!, Constant.DEL_N).takeIf { isMember ->
            !isMember
        }?.let {
            throw BaseException(StatusCode.NOT_PROJECT_MEMBER)
        }

        return workflowInfoRepository.findAllByProjectInfoIdAndDeleteYn(projectInfoId, Constant.DEL_N, pageable).map { workflowInfo ->
            getWorkFLowInfo(workflowInfo)
        }
    }

    fun getWorkflowContent(userInfo: UserInfo, workflowInfoId: Long): WorkFlowInfoResponseDTO {
        return workflowInfoRepository.findById(workflowInfoId).orElseThrow {
            throw BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { workflowInfo ->
            getWorkFLowInfo(workflowInfo).also {
                projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(it.projectInfoId, userInfo.id!!, Constant.DEL_N).takeIf { isMember ->
                    !isMember
                }?.let {
                    throw BaseException(StatusCode.NOT_PROJECT_MEMBER)
                }
            }
        }
    }

    @Transactional
    fun createWorkflowContent(userInfo: UserInfo, workFlowInfoRequestDTO: WorkFlowInfoRequestDTO) {
        val projectInfoId = workFlowInfoRequestDTO.projectInfoId

        // 프로젝트 존재 여부 판단
        if (!projectInfoRepository.existsByIdAndDeleteYn(projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }

        // 프로젝트 생성자 여부 판단
        if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId, userInfo.id!!, true, Constant.DEL_N)) {
            throw BaseException(StatusCode.NOT_PROJECT_CREATOR)
        }

        // step_info 정보 존재 여부 판단
        if (!stepInfoRepository.existsByIdAndProjectInfoIdAndDeleteYn(workFlowInfoRequestDTO.stepInfoId, projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
        }

        val workflowInfoEntity = workFlowInfoRequestDTO.toEntity()
        workflowInfoRepository.save(workflowInfoEntity)

        // 상세 정보 생성
        workflowDetailInfoRepository.save(WorkflowDetailInfoEntity(workflowInfoId = workflowInfoEntity.id!!))

        // 할당대상이 프로젝트에 소속된 멤버인지 판단
        workFlowInfoRequestDTO.assignInfo?.map {
            if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndDeleteYn(projectInfoId, it, Constant.DEL_N)) {
                throw BaseException(StatusCode.PROJECT_MEMBER_NOT_FOUND)
            }

            assignInfoRepository.save(AssignInfoDTO.toEntity(workflowInfoEntity.id!!, it))
        }
    }

    @Transactional
    fun modifyWorkflowContent(userInfo: UserInfo, workFlowInfoUpdateRequestDTO: WorkFlowInfoUpdateRequestDTO) {
        val projectInfoId = workFlowInfoUpdateRequestDTO.projectInfoId

        workflowInfoRepository.findByIdAndProjectInfoIdAndDeleteYn(workFlowInfoUpdateRequestDTO.workflowInfoId, projectInfoId, Constant.DEL_N).orElseThrow {
            throw BaseException(StatusCode.WORKFLOW_NOT_FOUND)
        }.let { workFlow ->
            // 프로젝트 생성자 여부 판단
            if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId, userInfo.id!!, true, Constant.DEL_N)) {
                throw BaseException(StatusCode.NOT_PROJECT_CREATOR)
            }

            workFlowInfoUpdateRequestDTO.let {
                it.stepInfoId?.let { stepInfoId ->
                    // step_info 정보 존재 여부 판단
                    if (!stepInfoRepository.existsByIdAndProjectInfoIdAndDeleteYn(stepInfoId, projectInfoId, Constant.DEL_N)) {
                        throw BaseException(StatusCode.WORKFLOW_STEP_NOT_FOUND)
                    }

                    workFlow.stepInfoId = stepInfoId
                }

                it.title?.let { title -> workFlow.title = title }
                it.content?.let { content -> workFlow.content = content }

                workflowInfoRepository.save(workFlow)
            }
        }
    }
}