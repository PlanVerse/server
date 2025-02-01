package com.planverse.server.team.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TeamProjectInfoDTO(
    var id: Long? = null,
    var teamMemberInfoId: Long,
    var projectInfoId: Long,
    var creator: Boolean,
)