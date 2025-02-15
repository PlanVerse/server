package com.planverse.server.project.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import com.planverse.server.project.dto.ProjectInfoRequestDTO
import com.planverse.server.project.dto.ProjectMemberInfoDTO
import com.planverse.server.project.mapper.ProjectInfoMapper
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.mapper.TeamMemberInfoMapper
import com.planverse.server.team.repository.TeamMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val fileService: FileService,

    private val teamMemberInfoRepository: TeamMemberInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,
    private val projectInfoRepository: ProjectInfoRepository,

    private val teamMemberInfoMapper: TeamMemberInfoMapper,
    private val projectInfoMapper: ProjectInfoMapper,
) {

    fun getProjectInfo(userInfo: UserInfo, projectId: Long): ProjectAndMemberAndTeamInfoDTO {
        val projectAndMemberAndTeamInfo: ProjectAndMemberAndTeamInfoDTO = checkNotNull(projectInfoMapper.selectProjectAndMemberAndTeamInfo(projectId)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.apply {
            if (this.teamInfo.private == true) {
                teamMemberInfoRepository.findAllByTeamInfoIdAndDeleteYn(this.teamInfo.id!!, Constant.DEL_N).orElseThrow {
                    BaseException(StatusCode.TEAM_MEMBER_NOT_FOUND)
                }.let {
                    if (!it.stream().map { member -> member.userInfoId }.toList().contains(userInfo.id)) {
                        throw BaseException(StatusCode.PROJECT_NOT_FOUND)
                    }
                }
            }

            this.projectProfileImage = fileService.getFile(Constant.FILE_TARGET_PROJECT, projectId)
        }

        return projectAndMemberAndTeamInfo
    }

    @Transactional
    fun createProject(userInfo: UserInfo, projectInfoRequestDTO: ProjectInfoRequestDTO, multipartFile: MultipartFile?) {
        val projectInfoId = projectInfoRepository.save(projectInfoRequestDTO.toEntity()).id!!
        val teamId = projectInfoRequestDTO.teamId

        teamMemberInfoMapper.selectTeamMemberInfoForCreator(TeamInfoUpdateRequestDTO(teamId = teamId, creatorUserInfoId = userInfo.id)).ifEmpty {
            throw BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let {
            buildList {
                it.forEach { member ->
                    if (member.creator) {
                        projectMemberInfoRepository.save(ProjectMemberInfoDTO.toEntity(projectInfoId, teamId, member.userInfoId, Constant.FLAG_TRUE))
                    } else {
                        add(member.teamInfoId)
                    }
                }
            }
        }

        projectInfoRequestDTO.invite?.forEach { inviteUserId ->
            if (inviteUserId == userInfo.id) {
                throw BaseException(StatusCode.PROJECT_CREATOR_IS_ALREADY_MEMBER)
            } else {
                projectMemberInfoRepository.save(ProjectMemberInfoDTO.toEntity(projectInfoId, teamId, inviteUserId, Constant.FLAG_FALSE))
            }
        }

        if (multipartFile != null && !multipartFile.isEmpty) {
            fileService.fileSave(Constant.FILE_TARGET_PROJECT, projectInfoId, multipartFile)
        }
    }
}