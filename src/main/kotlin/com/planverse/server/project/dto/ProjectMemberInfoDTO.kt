package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import com.planverse.server.project.entity.ProjectMemberInfoEntity
import com.planverse.server.user.entity.UserInfoEntity
import org.apache.ibatis.type.Alias

@MyBatisResponse
@Alias("ProjectMemberInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectMemberInfoDTO(
    var id: Long? = null,
    var projectInfoId: Long,
    var teamInfoId: Long,
    var userInfoId: Long,
    var creator: Boolean,

    var username: String? = null,
    var email: String? = null,
) {
    companion object {
        fun toDto(projectMemberInfoEntity: ProjectMemberInfoEntity): ProjectMemberInfoDTO {
            return ProjectMemberInfoDTO(
                id = projectMemberInfoEntity.id,
                projectInfoId = projectMemberInfoEntity.projectInfoId,
                teamInfoId = projectMemberInfoEntity.teamInfoId,
                userInfoId = projectMemberInfoEntity.userInfoId,
                creator = projectMemberInfoEntity.creator
            )
        }

        fun toDto(projectMemberInfoEntity: ProjectMemberInfoEntity, userInfoEntity: UserInfoEntity): ProjectMemberInfoDTO {
            return this.toDto(projectMemberInfoEntity).apply {
                username = userInfoEntity.name
                email = userInfoEntity.email
            }
        }

        fun toEntity(projectInfoId: Long, teamInfoId: Long, userInfoId: Long, creator: Boolean): ProjectMemberInfoEntity {
            return ProjectMemberInfoEntity(
                projectInfoId = projectInfoId,
                teamInfoId = teamInfoId,
                userInfoId = userInfoId,
                creator = creator,
            )
        }
    }
}