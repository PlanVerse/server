package com.planverse.server.project.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.project.dto.ProjectInfoDTO
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectTeamRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val fileService: FileService,
    private val userInfoRepository: UserInfoRepository,
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectTeamRepository: ProjectTeamRepository,
) {

    fun getProjectInfo(userInfo: UserInfo, projectId: Long): ProjectInfoDTO {
        // 프로젝트 정보 존재여부 판단
        val projectInfoDTO = projectInfoRepository.findById(projectId).orElseThrow {
            BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.let { ProjectInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFile(Constant.FILE_TARGET_PROJECT, projectId)

        // 프로젝트 멤버중 생성자와 동일한지 2차검증

        return projectInfoDTO.apply {
        }
    }
}