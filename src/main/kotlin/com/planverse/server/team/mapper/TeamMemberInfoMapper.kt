package com.planverse.server.team.mapper

import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface TeamMemberInfoMapper {
    // inset

    // update
    fun updateTeamMemberInfo()

    // select
    fun selectTeamMemberInfoForCreator(teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO): List<TeamMemberInfoDTO>

    // delete
}