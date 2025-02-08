package com.planverse.server.project.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import com.planverse.server.project.mapper.ProjectInfoMapper
import com.planverse.server.team.repository.TeamMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val fileService: FileService,
    private val teamMemberInfoRepository: TeamMemberInfoRepository,
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
}