package com.planverse.server.team.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import com.planverse.server.team.entity.TeamMemberInfoEntity
import com.planverse.server.team.mapper.TeamMemberInfoMapper
import com.planverse.server.team.repository.TeamInfoRepository
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
class TeamService(
    private val fileService: FileService,
    private val userInfoRepository: UserInfoRepository,
    private val teamInfoRepository: TeamInfoRepository,
    private val teamMemberInfoRepository: TeamMemberInfoRepository,
    private val teamMemberInfoMapper: TeamMemberInfoMapper,
) {

    private fun getCreatorDTO(teamMemberId: Long): TeamMemberInfoDTO {
        return teamMemberInfoRepository.findByTeamInfoIdAndCreatorAndDeleteYn(teamMemberId, true, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { creator ->
            val creatorUserInfo = userInfoRepository.findById(creator.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(creator, creatorUserInfo.name, creatorUserInfo.email)
        }
    }

    private fun getMemberDTOs(teamMemberId: Long): List<TeamMemberInfoDTO> {
        return teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamMemberId, false, Constant.DEL_N).orElse(emptyList()).map { member ->
            val memberUserInfo = userInfoRepository.findById(member.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(member, memberUserInfo.name, memberUserInfo.email)
        }
    }

    private fun getCreateTeamInfoDTO(userInfoId: Long, creator: TeamMemberInfoEntity): TeamInfoDTO {
        // 팀 정보 조회
        val teamInfoDTO = teamInfoRepository.findById(creator.teamInfoId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFileUrl(Constant.FILE_TARGET_TEAM, creator.teamInfoId)

        // 팀 생성자 정보 조회 및 변환
        val creatorDTO = getCreatorDTO(creator.teamInfoId)

        if (teamInfoDTO.private == true) {
            if (creatorDTO.userInfoId != userInfoId) {
                throw BaseException(StatusCode.TEAM_NOT_FOUND)
            }
        }

        // 팀 멤버 정보 조회 및 변환
        val memberDTOs = getMemberDTOs(creator.teamInfoId)

        // DTO 생성 및 반환
        return teamInfoDTO.apply {
            this.teamProfileImage = teamProfileImage
            this.teamCreatorInfo = creatorDTO
            this.teamMemberInfos = memberDTOs
        }
    }

    fun getTeamInfo(userInfo: UserInfo, teamId: Long): TeamInfoDTO {
        // 팀 정보 존재여부 판단
        val teamInfoDTO = teamInfoRepository.findById(teamId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFileUrl(Constant.FILE_TARGET_TEAM, teamId)

        // 팀 생성자 정보 조회
        val creatorDTO = getCreatorDTO(teamInfoDTO.id!!)

        // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
        val memberDTOs = getMemberDTOs(teamInfoDTO.id!!)

        if (teamInfoDTO.private == true) {
            val creatorAndMemberUserInfoIds = arrayListOf(creatorDTO.userInfoId, memberDTOs.stream().map { it.userInfoId }.toList())

            if (!creatorAndMemberUserInfoIds.contains(userInfo.id)) {
                throw BaseException(StatusCode.TEAM_NOT_FOUND)
            }
        }

        return teamInfoDTO.apply {
            this.teamProfileImage = teamProfileImage
            this.teamCreatorInfo = creatorDTO
            this.teamMemberInfos = memberDTOs
        }
    }

    fun getTeamListCreator(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, true, Constant.DEL_N, pageable).map { creator ->
            getCreateTeamInfoDTO(userInfo.id!!, creator)
        }
    }

    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, false, Constant.DEL_N, pageable).map { member ->
            // 팀 정보 조회
            val teamInfoDTO = teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { TeamInfoDTO.toDto(it) }

            val teamProfileImage = fileService.getFileUrl(Constant.FILE_TARGET_TEAM, member.teamInfoId)

            // 팀 생성자 조회
            val creatorDTO = getCreatorDTO(member.teamInfoId)

            // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
            val memberDTOs = getMemberDTOs(member.teamInfoId)

            if (teamInfoDTO.private == true) {
                val creatorAndMemberUserInfoIds = arrayListOf(creatorDTO.userInfoId, memberDTOs.stream().map { it.userInfoId }.toList())

                if (!creatorAndMemberUserInfoIds.contains(userInfo.id)) {
                    throw BaseException(StatusCode.TEAM_NOT_FOUND)
                }
            }

            teamInfoDTO.apply {
                this.teamProfileImage = teamProfileImage
                this.teamCreatorInfo = creatorDTO
                this.teamMemberInfos = memberDTOs
            }
        }
    }

    @Transactional
    fun createTeam(userInfo: UserInfo, teamInfoRequestDTO: TeamInfoRequestDTO) {
        val teamId = teamInfoRepository.save(teamInfoRequestDTO.toEntity()).id

        teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(userInfo.id!!, teamId!!, true))

        teamInfoRequestDTO.invite?.forEach { inviteEmail ->
            if (inviteEmail == userInfo.email) {
                throw BaseException(StatusCode.TEAM_CREATOR_IS_ALREADY_MEMBER)
            } else {
                userInfoRepository.findByEmailAndDeleteYn(inviteEmail, Constant.DEL_N).ifPresent { inviteUserInfo ->
                    val teamMemberInfoEntity = TeamMemberInfoDTO.toEntity(inviteUserInfo.id!!, teamId, false)
                    teamMemberInfoRepository.save(teamMemberInfoEntity)
                }
            }
        }
    }

    @Transactional
    fun modifyTeamInfo(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoUpdateRequestDTO.teamId, userInfo.id!!, true, Constant.DEL_N)
            .orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { member ->
                teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                    BaseException(StatusCode.TEAM_NOT_FOUND)
                }.let { info ->
                    if (teamInfoUpdateRequestDTO.name != null) {
                        info.name = teamInfoUpdateRequestDTO.name
                    }
                    info.description = teamInfoUpdateRequestDTO.description

                    teamInfoRepository.save(info)

                    this.inviteTeamMember(userInfo, teamInfoUpdateRequestDTO)
                }
            }
    }

    @Transactional
    fun inviteTeamMember(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        // 요청자의 정보가 생성자 즉 관리자의 권한일 경우에만 조회 값이 존재
        teamInfoUpdateRequestDTO.creatorUserInfoId = userInfo.id
        teamMemberInfoMapper.selectTeamMemberInfoForCreator(teamInfoUpdateRequestDTO).ifEmpty {
            throw BaseException(StatusCode.TEAM_NOT_FOUND)
        }.run {
            // 초대 멤버 추가 처리
            teamInfoUpdateRequestDTO.invite?.takeIf {
                it.isNotEmpty()
            }?.let { inviteList ->
                val existingMemberIds = this.map { it.userInfoId }.toSet()

                inviteList.map { email ->
                    userInfoRepository.findByEmail(email).orElseThrow {
                        BaseException(StatusCode.USER_NOT_FOUND)
                    }.id!!
                }.filterNot {
                    it in existingMemberIds
                }.forEach { newUserId ->
                    val newMember = TeamMemberInfoDTO.toEntity(newUserId, teamInfoUpdateRequestDTO.teamId, false)
                    teamMemberInfoRepository.save(newMember)
                }
            }

            // 멤버 제외 처리
            teamInfoUpdateRequestDTO.exclude?.takeIf {
                it.isNotEmpty()
            }?.let { excludeList ->
                val existingMemberIds = this.map { it.userInfoId }.toSet()

                excludeList.map { email ->
                    userInfoRepository.findByEmail(email).orElseThrow {
                        BaseException(StatusCode.USER_NOT_FOUND)
                    }.id!!
                }.filter {
                    it in existingMemberIds
                }.forEach { userIdToExclude ->
                    val member = teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(
                        teamInfoUpdateRequestDTO.teamId,
                        userIdToExclude,
                        true,
                        Constant.DEL_N
                    ).orElseThrow {
                        BaseException(StatusCode.TEAM_CREATOR_CANNOT_EXCLUDE)
                    }

                    member.deleteYn = Constant.DEL_Y
                    teamMemberInfoRepository.save(member)
                }
            }
        }
    }

    @Transactional
    fun modifyTeamImage(userInfo: UserInfo, teamId: Long, multipartFile: MultipartFile) {
        teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamId, userInfo.id!!, true, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let {
            fileService.deleteFilePass(Constant.FILE_TARGET_TEAM, teamId).also {
                fileService.fileSave(Constant.FILE_TARGET_TEAM, teamId, multipartFile)
            }
        }
    }
}