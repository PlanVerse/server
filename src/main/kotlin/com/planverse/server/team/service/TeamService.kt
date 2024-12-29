package com.planverse.server.team.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import com.planverse.server.team.repository.TeamInfoRepository
import com.planverse.server.team.repository.TeamMemberRepository
import com.planverse.server.user.dto.UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TeamService(
    private val teamInfoRepository: TeamInfoRepository,
    private val teamMemberRepository: TeamMemberRepository,
) {

    fun getTeamInfo(userInfo: UserInfo, teamId: Long) {
        // 팀 정보 존재여부 판단
        val teamInfoDTO = teamInfoRepository.findById(teamId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        // 팀 멤버중 생성자와 동일한지 2차검증
        val teamCreatorInfoDTO = teamMemberRepository.findByTeamInfoIdAndUserInfoIdAndCreator(teamInfoDTO.id!!, userInfo.id, Constant.FLAG_TRUE).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamMemberInfoDTO.toDto(it) }

        // fixme DTO 변환이 안되고있으니 수정해야함
        // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
        val teamMemberInfoDTOList: List<TeamMemberInfoDTO> = buildList {
            teamMemberRepository.findByTeamInfoIdAndCreator(teamInfoDTO.id!!, Constant.FLAG_FALSE).orElse(emptyList()).forEach {
                TeamMemberInfoDTO.toDto(it)
            }
        }

        println(123)
    }

    fun getTeamList() {
    }
}