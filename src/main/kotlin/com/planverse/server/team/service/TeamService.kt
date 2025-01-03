package com.planverse.server.team.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import com.planverse.server.team.repository.TeamInfoRepository
import com.planverse.server.team.repository.TeamMemberRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TeamService(
    private val userInfoRepository: UserInfoRepository,
    private val teamInfoRepository: TeamInfoRepository,
    private val teamMemberRepository: TeamMemberRepository,
) {

    fun getTeamInfo(userInfo: UserInfo, teamId: Long): TeamInfoDTO {
        // 팀 정보 존재여부 판단
        val teamInfoDTO = teamInfoRepository.findById(teamId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        // 팀 멤버중 생성자와 동일한지 2차검증
        val teamCreatorInfoDTO = teamMemberRepository.findByTeamInfoIdAndUserInfoIdAndCreator(teamInfoDTO.id!!, userInfo.id, Constant.FLAG_TRUE).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let {
            TeamMemberInfoDTO.toDto(it)
        }

        // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
        val teamMemberInfoDTOList: List<TeamMemberInfoDTO> = buildList {
            teamMemberRepository.findByTeamInfoIdAndCreator(teamInfoDTO.id!!, Constant.FLAG_FALSE).orElse(emptyList()).forEach {
                add(TeamMemberInfoDTO.toDto(it))
            }
        }

        return teamInfoDTO.apply {
            this.teamCreatorInfo = teamCreatorInfoDTO
            this.teamMemberInfos = teamMemberInfoDTOList
        }
    }

    fun getTeamListCreator(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        return teamMemberRepository.findAllByUserInfoIdAndCreator(userInfo.id, Constant.FLAG_TRUE, pageable).map { member ->
            teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { TeamInfoDTO.toDtoAndCreator(it, TeamMemberInfoDTO.toDto(member)) }
        }
    }

    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        return teamMemberRepository.findAllByUserInfoIdAndCreator(userInfo.id, Constant.FLAG_FALSE, pageable).map { member ->
            teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { TeamInfoDTO.toDtoAndMember(it, TeamMemberInfoDTO.toDto(member)) }
        }
    }

    @Transactional
    fun createTeam(userInfo: UserInfo, teamInfoRequestDTO: TeamInfoRequestDTO) {
        val teamId = teamInfoRepository.save(teamInfoRequestDTO.toEntity()).id

        teamMemberRepository.save(TeamMemberInfoDTO.toEntity(userInfo.id, teamId!!, Constant.FLAG_TRUE))

        teamInfoRequestDTO.invite?.forEach { inviteEmail ->
            if (inviteEmail == userInfo.email) {
                throw BaseException(StatusCode.TEAM_CREATOR_IS_ALREADY_MEMBER)
            } else {
                userInfoRepository.findByEmailAndDeleteYn(inviteEmail, Constant.DEL_N).ifPresent { creatorUserInfo ->
                    val teamMemberInfoEntity = TeamMemberInfoDTO.toEntity(creatorUserInfo.id!!, teamId, Constant.FLAG_FALSE)
                    teamMemberRepository.save(teamMemberInfoEntity)
                }
            }
        }
    }
}