package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import com.planverse.server.team.dto.TeamInfoDTO
import org.apache.ibatis.type.Alias

@MyBatisResponse
@Alias("ProjectAndMemberAndTeamInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectAndMemberAndTeamInfoDTO(
    var id: Long? = null,
    var key: String,
    var name: String,
    var description: String? = null,
    var projectProfileImage: String? = null,

    var teamInfo: TeamInfoDTO,

    var projectMemberInfos: List<ProjectMemberInfoDTO>,
)