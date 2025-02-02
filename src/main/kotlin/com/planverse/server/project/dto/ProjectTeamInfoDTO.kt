package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectTeamInfoDTO(
    var id: Long? = null,
    var teamMemberInfoId: Long,
    var projectInfoId: Long,
    var creator: Boolean,
)