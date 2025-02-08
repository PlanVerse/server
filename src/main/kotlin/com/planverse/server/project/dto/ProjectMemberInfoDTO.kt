package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
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
)