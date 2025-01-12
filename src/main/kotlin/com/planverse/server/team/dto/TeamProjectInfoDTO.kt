package com.planverse.server.team.dto

data class TeamProjectInfoDTO(
    var id: Long? = null,
    var teamMemberInfoId: Long,
    var projectInfoId: Long,
    var creator: Boolean,
)