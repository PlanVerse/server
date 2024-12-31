package com.planverse.server.team.dto

import com.planverse.server.team.entity.TeamInfoEntity

data class TeamInfoDTO(
    var id: Long? = null,
    var key: String,
    var name: String,
    var description: String? = null,
    var teamCreatorInfo: TeamMemberInfoDTO? = null,
    var teamMemberInfo: List<TeamMemberInfoDTO>? = null,
) {

    companion object {
        fun toDto(teamInfoEntity: TeamInfoEntity): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key,
                teamInfoEntity.name,
                teamInfoEntity.description,
            )
        }
    }
}