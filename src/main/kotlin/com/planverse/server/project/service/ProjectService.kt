package com.planverse.server.project.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import com.planverse.server.project.dto.ProjectInfoRequestDTO
import com.planverse.server.project.dto.ProjectInfoUpdateRequestDTO
import com.planverse.server.project.dto.ProjectMemberInfoDTO
import com.planverse.server.project.mapper.ProjectInfoMapper
import com.planverse.server.project.mapper.ProjectMemberInfoMapper
import com.planverse.server.project.repository.ProjectInfoRepository
import com.planverse.server.project.repository.ProjectMemberInfoRepository
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.mapper.TeamMemberInfoMapper
import com.planverse.server.team.repository.TeamMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val fileService: FileService,

    private val userInfoRepository: UserInfoRepository,

    private val teamMemberInfoRepository: TeamMemberInfoRepository,
    private val projectInfoRepository: ProjectInfoRepository,
    private val projectMemberInfoRepository: ProjectMemberInfoRepository,

    private val teamMemberInfoMapper: TeamMemberInfoMapper,
    private val projectInfoMapper: ProjectInfoMapper,
    private val projectMemberInfoMapper: ProjectMemberInfoMapper,
) {

    fun getProjectInfo(userInfo: UserInfo, projectInfoId: Long): ProjectAndMemberAndTeamInfoDTO {
        val projectAndMemberAndTeamInfo: ProjectAndMemberAndTeamInfoDTO = checkNotNull(projectInfoMapper.selectProjectAndMemberAndTeamInfo(projectInfoId)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.apply {
            if (this.teamInfo?.private == true) {
                teamMemberInfoRepository.findAllByTeamInfoIdAndDeleteYn(this.teamInfo!!.id!!, Constant.DEL_N).orElseThrow {
                    BaseException(StatusCode.TEAM_MEMBER_NOT_FOUND)
                }.let {
                    if (!it.stream().map { member -> member.userInfoId }.toList().contains(userInfo.id)) {
                        throw BaseException(StatusCode.PROJECT_NOT_FOUND)
                    }
                }
            }

            this.projectProfileImage = fileService.getFileUrl(Constant.FILE_TARGET_PROJECT, projectInfoId)
        }

        return projectAndMemberAndTeamInfo
    }

    fun getProjectInfoList(userInfo: UserInfo, teamInfoId: Long, pageable: Pageable): Slice<ProjectAndMemberAndTeamInfoDTO> {
        return projectInfoRepository.findAllByTeamInfoIdAndDeleteYn(teamInfoId, Constant.DEL_N, pageable).map {
            val projectProfileImage = fileService.getFileUrl(Constant.FILE_TARGET_PROJECT, it.id!!)

            val projectMemberInfos: List<ProjectMemberInfoDTO> = projectMemberInfoRepository.findAllByProjectInfoId(it.id!!).orElse(emptyList()).map { entity ->
                val userInfoEntity = userInfoRepository.findById(entity.userInfoId).orElseThrow {
                    BaseException(StatusCode.USER_NOT_FOUND)
                }
                ProjectMemberInfoDTO.toDto(entity, userInfoEntity)
            }

            ProjectAndMemberAndTeamInfoDTO(
                id = it.id,
                key = it.key!!,
                name = it.name,
                description = it.description,
                projectProfileImage = projectProfileImage,
                projectMemberInfos = projectMemberInfos,
            )
        }
    }

    @Transactional
    fun createProject(userInfo: UserInfo, projectInfoRequestDTO: ProjectInfoRequestDTO) {
        val projectInfoId = projectInfoRepository.save(projectInfoRequestDTO.toEntity()).id!!
        val teamId = projectInfoRequestDTO.teamInfoId

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
    }

    @Transactional
    fun modifyProjectInfo(userInfo: UserInfo, projectInfoUpdateRequestDTO: ProjectInfoUpdateRequestDTO) {
        projectMemberInfoRepository.findByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoUpdateRequestDTO.projectInfoId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.let { member ->
            projectInfoRepository.findById(member.projectInfoId).orElseThrow {
                BaseException(StatusCode.PROJECT_NOT_FOUND)
            }.let { info ->
                if (projectInfoUpdateRequestDTO.name != null) {
                    info.name = projectInfoUpdateRequestDTO.name
                }
                info.description = projectInfoUpdateRequestDTO.description

                projectInfoRepository.save(info)
            }
        }
    }

    @Transactional
    fun inviteProjectMember(userInfo: UserInfo, projectInfoUpdateRequestDTO: ProjectInfoUpdateRequestDTO) {
        projectInfoUpdateRequestDTO.creatorUserInfoId = userInfo.id
        projectMemberInfoMapper.selectProjectMemberInfoForCreator(projectInfoUpdateRequestDTO).ifEmpty {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.run {
            val teamInfoId = this[0].teamInfoId
            val teamMembers = teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamInfoId, Constant.FLAG_FALSE, Constant.DEL_N).orElseThrow {
                BaseException(StatusCode.TEAM_MEMBER_NOT_FOUND)
            }.let {
                if (!it.stream().map { member -> member.userInfoId }.toList().contains(userInfo.id)) {
                    throw BaseException(StatusCode.PROJECT_NOT_FOUND)
                }

                it.stream().map { member -> member.userInfoId }.toList()
            }

            if (projectInfoUpdateRequestDTO.invite != null) {
                buildList {
                    projectInfoUpdateRequestDTO.invite.forEach {
                        val inviteUserInfo = userInfoRepository.findByEmail(it).orElseThrow {
                            BaseException(StatusCode.USER_NOT_FOUND)
                        }

                        add(inviteUserInfo.id!!)
                    }
                }.filter {
                    it !in this.stream().map { member -> member.userInfoId }.toList()
                }.filter {
                    it !in teamMembers.stream().map { teamMember -> teamMember }.toList()
                }.forEach {
                    projectMemberInfoRepository.save(ProjectMemberInfoDTO.toEntity(projectInfoUpdateRequestDTO.projectInfoId, teamInfoId, it, Constant.FLAG_FALSE))
                }
            }

            if (projectInfoUpdateRequestDTO.exclude != null) {
                buildList {
                    projectInfoUpdateRequestDTO.exclude.forEach {
                        val inviteUserInfo = userInfoRepository.findByEmail(it).orElseThrow {
                            BaseException(StatusCode.USER_NOT_FOUND)
                        }

                        add(inviteUserInfo.id!!)
                    }
                }.filter {
                    it !in this.stream().map { member -> member.userInfoId }.toList()
                }.filter {
                    it !in teamMembers.stream().map { teamMember -> teamMember }.toList()
                }.forEach {
                    projectMemberInfoRepository.findByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoUpdateRequestDTO.projectInfoId, it, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
                        BaseException(StatusCode.TEAM_CREATOR_CANNOT_EXCLUDE)
                    }.let { excludeMemberInfo ->
                        excludeMemberInfo.deleteYn = Constant.DEL_Y
                        projectMemberInfoRepository.save(excludeMemberInfo)
                    }
                }
            }
        }
    }

    @Transactional
    fun modifyProjectImage(userInfo: UserInfo, projectInfoId: Long, multipartFile: MultipartFile) {
        projectMemberInfoRepository.findByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.PROJECT_NOT_FOUND)
        }.let {
            fileService.deleteFilePass(Constant.FILE_TARGET_PROJECT, projectInfoId).also {
                fileService.fileSave(Constant.FILE_TARGET_PROJECT, projectInfoId, multipartFile)
            }
        }
    }
}