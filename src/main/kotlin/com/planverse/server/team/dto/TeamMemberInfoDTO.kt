package com.planverse.server.team.dto

import com.planverse.server.team.entity.TeamMemberInfoEntity

data class TeamMemberInfoDTO(
    var id: Long? = null,
    var userInfoId: Long,
    var teamInfoId: Long,
    var creator: Boolean,
) {
    companion object {
        fun toDto(teamMemberInfoEntity: TeamMemberInfoEntity): TeamMemberInfoDTO {
            return TeamMemberInfoDTO(
                teamMemberInfoEntity.id,
                teamMemberInfoEntity.userInfoId,
                teamMemberInfoEntity.teamInfoId,
                teamMemberInfoEntity.creator,
            )
        }
    }
}