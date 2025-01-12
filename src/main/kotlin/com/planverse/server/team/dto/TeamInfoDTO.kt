package com.planverse.server.team.dto

import com.planverse.server.team.entity.TeamInfoEntity

data class TeamInfoDTO(
    var id: Long? = null,
    var key: String,
    var name: String,
    var description: String? = null,
    var teamProfileImage: String? = null,
    var teamCreatorInfo: TeamMemberInfoDTO? = null,
    var teamMemberInfo: TeamMemberInfoDTO? = null,
    var teamCreatorInfos: List<TeamMemberInfoDTO>? = null,
    var teamMemberInfos: List<TeamMemberInfoDTO>? = null,
) {
    companion object {
        fun toDto(teamInfoEntity: TeamInfoEntity): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
            )
        }

        fun toDtoAndCreator(teamInfoEntity: TeamInfoEntity, teamMemberInfoDTO: TeamMemberInfoDTO): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamCreatorInfo = teamMemberInfoDTO,
            )
        }

        fun toDtoAndMember(teamInfoEntity: TeamInfoEntity, teamMemberInfoDTO: TeamMemberInfoDTO): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamMemberInfo = teamMemberInfoDTO,
            )
        }

        fun toDtoAndCreatorAndMember(teamInfoEntity: TeamInfoEntity, teamCreatorInfo: TeamMemberInfoDTO, teamMemberInfoDTOs: List<TeamMemberInfoDTO>): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamCreatorInfo = teamCreatorInfo,
                teamMemberInfos = teamMemberInfoDTOs,
            )
        }
    }
}