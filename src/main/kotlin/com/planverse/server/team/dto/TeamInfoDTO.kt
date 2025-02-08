package com.planverse.server.team.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import com.planverse.server.team.entity.TeamInfoEntity
import org.apache.ibatis.type.Alias

@MyBatisResponse
@Alias("TeamInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TeamInfoDTO(
    var id: Long? = null,
    var key: String,
    var name: String,
    var description: String? = null,
    val private: Boolean? = false,
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
                private = teamInfoDTO.private,
            )
        }

        fun toDto(teamInfoEntity: TeamInfoEntity): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamInfoEntity.private,
            )
        }

        fun toDtoAndCreator(teamInfoEntity: TeamInfoEntity, teamMemberInfoDTO: TeamMemberInfoDTO): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamInfoEntity.private,
                teamCreatorInfo = teamMemberInfoDTO,
            )
        }

        fun toDtoAndMember(teamInfoEntity: TeamInfoEntity, teamMemberInfoDTO: TeamMemberInfoDTO): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamInfoEntity.private,
                teamMemberInfo = teamMemberInfoDTO,
            )
        }

        fun toDtoAndCreatorAndMember(teamInfoEntity: TeamInfoEntity, teamCreatorInfo: TeamMemberInfoDTO, teamMemberInfoDTOs: List<TeamMemberInfoDTO>): TeamInfoDTO {
            return TeamInfoDTO(
                teamInfoEntity.id,
                teamInfoEntity.key!!,
                teamInfoEntity.name,
                teamInfoEntity.description,
                teamInfoEntity.private,
                teamCreatorInfo = teamCreatorInfo,
                teamMemberInfos = teamMemberInfoDTOs,
            )
        }
    }
}