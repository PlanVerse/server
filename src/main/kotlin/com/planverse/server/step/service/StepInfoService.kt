package com.planverse.server.step.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.step.dto.StepInfoRequestDTO
import com.planverse.server.step.dto.StepInfoResponseDTO
import com.planverse.server.step.repository.StepInfoRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StepInfoService(
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val stepInfoRepository: StepInfoRepository,
) {
    fun getStepInfoList(userInfo: UserInfo, projectInfoId: Long): List<StepInfoResponseDTO> {
        return buildList {
            stepInfoRepository.findAllByProjectInfoIdAndDeleteYnOrderBySort(projectInfoId, Constant.DEL_N).ifPresent {
                it.map { stepInfo ->
                    add(StepInfoResponseDTO.toDto(stepInfo))
                }
            }
        }
    }

    @Transactional
    fun createStepInfo(userInfo: UserInfo, stepInfoRequestDTO: StepInfoRequestDTO) {
        // 프로젝트 존재 여부 판단
        if (!projectInfoRepository.existsByIdAndDeleteYn(stepInfoRequestDTO.projectInfoId, Constant.DEL_N)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }

        // 프로젝트 생성자 여부 판단
        if (!projectMemberInfoRepository.existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(stepInfoRequestDTO.projectInfoId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N)) {
            throw BaseException(StatusCode.NOT_PROJECT_CREATOR)
        }

        // step_info 정보 존재 여부 판단
        if (!stepInfoRepository.existsByProjectInfoIdAndNameAndDeleteYn(stepInfoRequestDTO.projectInfoId, stepInfoRequestDTO.name, Constant.DEL_N)) {
            stepInfoRepository.save(stepInfoRequestDTO.toEntity())
        } else {
            throw BaseException(StatusCode.ALREADY_EXIST_STEP)
        }
    }
}