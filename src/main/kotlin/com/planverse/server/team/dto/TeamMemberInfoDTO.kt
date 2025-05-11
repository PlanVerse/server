package com.planverse.server.team.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import com.planverse.server.team.entity.TeamMemberInfoEntity
import org.apache.ibatis.type.Alias

@MyBatisResponse
@Alias("TeamMemberInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TeamMemberInfoDTO(
    var id: Long? = null,
    var userInfoId: Long,
    var teamInfoId: Long,
    var creator: Boolean,
    var username: String? = null,
    var email: String? = null,
) {
    companion object {
        fun toDto(teamMemberInfoEntity: TeamMemberInfoEntity) = TeamMemberInfoDTO(
            teamMemberInfoEntity.id,
            teamMemberInfoEntity.userInfoId,
            teamMemberInfoEntity.teamInfoId,
            teamMemberInfoEntity.creator,
        )

        fun toDto(teamMemberInfoEntity: TeamMemberInfoEntity, username: String, email: String) = TeamMemberInfoDTO(
            teamMemberInfoEntity.id,
            teamMemberInfoEntity.userInfoId,
            teamMemberInfoEntity.teamInfoId,
            teamMemberInfoEntity.creator,
            username,
            email,
        )

        fun toEntity(userInfoId: Long, teamInfoId: Long, creator: Boolean) = TeamMemberInfoEntity(
            userInfoId = userInfoId,
            teamInfoId = teamInfoId,
            creator = creator,
        )
    }
}