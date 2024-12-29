package com.planverse.server.team.dto

import com.planverse.server.team.entity.TeamInfoEntity

data class TeamInfoDTO(
    var id: Long? = null,
    val key: String,
    val name: String,
    val description: String? = null,
) {
    companion object {
        fun toDto(teamInfoEntity: TeamInfoEntity): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key,
                teamInfoEntity.name,
                teamInfoEntity.description
            )
        }
    }
}