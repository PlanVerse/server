package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.project.entity.ProjectInfoEntity
import com.planverse.server.team.entity.TeamInfoEntity
import org.apache.ibatis.type.Alias

@Alias("ProjectInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectInfoDTO(
    var id: Long? = null,
    var key: String? = null,
    var name: String,
    var description: String? = null,
    var projectProfileImage: String? = null,
) {
    companion object {
        fun toEntity(projectInfoDTO: ProjectInfoDTO): TeamInfoEntity {
            return TeamInfoEntity(
                id = projectInfoDTO.id,
                key = projectInfoDTO.key,
                name = projectInfoDTO.name,
                description = projectInfoDTO.description,
            )
        }

        fun toDto(projectInfoEntity: ProjectInfoEntity): ProjectInfoDTO {
            return ProjectInfoDTO(
                projectInfoEntity.id,
                projectInfoEntity.key,
                projectInfoEntity.name,
                projectInfoEntity.description,
            )
        }
    }
}