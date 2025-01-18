package com.planverse.server.team.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.team.entity.TeamInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
        fun toEntity(teamInfoDTO: TeamInfoDTO): TeamInfoEntity {
            return TeamInfoEntity(
                id = teamInfoDTO.id,
                key = teamInfoDTO.key,
                name = teamInfoDTO.name,
                description = teamInfoDTO.description,
            )
        }

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