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
) {

    fun getTeamInfo(userInfo: UserInfo, teamId: Long): TeamInfoDTO {
        // 팀 정보 존재여부 판단
        val teamInfoDTO = teamInfoRepository.findById(teamId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFile(Constant.FILE_TARGET_TEAM, teamId)

        // 팀 멤버중 생성자와 동일한지 2차검증
        val teamCreatorInfoDTO =
            teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoDTO.id!!, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let {
                TeamMemberInfoDTO.toDto(it)
            }

        // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
        val teamMemberInfoDTOList: List<TeamMemberInfoDTO> = buildList {
            teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamInfoDTO.id!!, Constant.FLAG_FALSE, Constant.DEL_N).orElse(emptyList()).forEach {
                add(TeamMemberInfoDTO.toDto(it))
            }
        }

        return teamInfoDTO.apply {
            this.teamProfileImage = teamProfileImage
            this.teamCreatorInfo = teamCreatorInfoDTO
            this.teamMemberInfos = teamMemberInfoDTOList
        }
    }

    fun getTeamListCreator(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N, pageable).map { creator ->
            getCreateTeamInfoDTO(creator)
        }
    }

    private fun getCreateTeamInfoDTO(creator: TeamMemberInfoEntity): TeamInfoDTO {
        // 팀 정보 조회
        val teamInfo = teamInfoRepository.findById(creator.teamInfoId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }

        // 생성자 정보 조회
        val creatorUserInfo = userInfoRepository.findById(creator.userInfoId).orElseThrow {
            BaseException(StatusCode.USER_NOT_FOUND)
        }

        // 팀 멤버 정보 조회 및 변환
        val memberDTOs = getMemberDTOs(creator.id!!)

        // DTO 생성 및 반환
        return TeamInfoDTO.toDtoAndCreatorAndMember(
            teamInfo,
            TeamMemberInfoDTO.toDto(creator, creatorUserInfo.name),
            memberDTOs
        )
    }

    private fun getMemberDTOs(teamMemberId: Long): List<TeamMemberInfoDTO> {
        return teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamMemberId, Constant.FLAG_FALSE, Constant.DEL_N).orElse(emptyList()).map { member ->
            val memberUserInfo = userInfoRepository.findById(member.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(member, memberUserInfo.name)
        }
    }

    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, Constant.FLAG_FALSE, Constant.DEL_N, pageable).map { member ->
            getTeamCreatorInfoDTO(member)
        }
    }

    private fun getTeamCreatorInfoDTO(member: TeamMemberInfoEntity): TeamInfoDTO {
        // 팀 정보 조회
        val teamInfo = teamInfoRepository.findById(member.teamInfoId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }

        // 팀 생성자 조회
        val teamCreatorInfo = teamMemberInfoRepository.findByTeamInfoIdAndCreatorAndDeleteYn(member.teamInfoId, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow{
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }

        // 생성자 정보 조회
        val creatorUserInfo = userInfoRepository.findById(teamCreatorInfo.userInfoId).orElseThrow {
            BaseException(StatusCode.USER_NOT_FOUND)
        }

        // DTO 생성 및 반환
        return TeamInfoDTO.toDtoAndCreator(
            teamInfo,
            TeamMemberInfoDTO.toDto(teamCreatorInfo, creatorUserInfo.name),
        )
    }

    @Transactional
    fun createTeam(userInfo: UserInfo, teamInfoRequestDTO: TeamInfoRequestDTO, multipartFile: MultipartFile?) {
        val teamId = teamInfoRepository.save(teamInfoRequestDTO.toEntity()).id

        teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(userInfo.id!!, teamId!!, Constant.FLAG_TRUE))

        teamInfoRequestDTO.invite?.forEach { inviteEmail ->
            if (inviteEmail == userInfo.email) {
                throw BaseException(StatusCode.TEAM_CREATOR_IS_ALREADY_MEMBER)
            } else {
                userInfoRepository.findByEmailAndDeleteYn(inviteEmail, Constant.DEL_N).ifPresent { creatorUserInfo ->
                    val teamMemberInfoEntity = TeamMemberInfoDTO.toEntity(creatorUserInfo.id!!, teamId, Constant.FLAG_FALSE)
                    teamMemberInfoRepository.save(teamMemberInfoEntity)
                }
            }
        }

        if (multipartFile != null && !multipartFile.isEmpty) {
            fileService.fileSave("team", teamId, multipartFile)
        }
    }

    @Transactional
    fun modifyTeamInfo(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        val teamInfoId = teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoUpdateRequestDTO.teamId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.teamInfoId

        val teamInfo = teamInfoRepository.findById(teamInfoId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }

        if (teamInfoUpdateRequestDTO.name != null) {
            teamInfo.name = teamInfoUpdateRequestDTO.name
        }
        teamInfo.description = teamInfoUpdateRequestDTO.description

        teamInfoRepository.save(teamInfo)
    }

    @Transactional
    fun inviteTeamMember(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        teamMemberInfoRepository.findAllByTeamInfoIdAndDeleteYn(teamInfoUpdateRequestDTO.teamId, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.run {
            // 팀 정보 존재여부 2차검증
            teamInfoRepository.findById(teamInfoUpdateRequestDTO.teamId).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }

            // 초대 멤버 정보가 없다면 동일한 것으로 판단
            if (teamInfoUpdateRequestDTO.invite != null) {
                buildList {
                    // 초대 멤버 사용자 정보 획득
                    teamInfoUpdateRequestDTO.invite.forEach {
                        val inviteUserInfo = userInfoRepository.findByEmail(it).orElseThrow {
                            BaseException(StatusCode.USER_NOT_FOUND)
                        }

                        // DB의 값이므로 존재 확인
                        add(inviteUserInfo.id!!)
                    }
                }.filter {
                    // 신규 멤버의 사용자 id에 기존 멤버 id가 포함되어있다면 제외
                    it !in this.stream().map { member -> member.userInfoId }.toList()
                }.forEach {
                    // 신규 멤버 추가
                    teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(it, teamInfoUpdateRequestDTO.teamId, Constant.FLAG_FALSE))
                }
            }
        }
    }
}