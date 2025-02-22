package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import com.planverse.server.project.entity.ProjectMemberInfoEntity
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

    var username: String,
    var email: String,
) {
    companion object {
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