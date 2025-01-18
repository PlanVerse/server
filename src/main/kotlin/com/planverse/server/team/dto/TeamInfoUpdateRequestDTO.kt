package com.planverse.server.team.dto

import jakarta.validation.constraints.NotBlank
import org.apache.ibatis.type.Alias

@Alias("TeamInfoUpdateRequestDTO")
data class TeamInfoUpdateRequestDTO(
    @field:NotBlank
    val teamId: Long,
    val name: String? = null,
    val description: String? = null,
    val invite: List<String>? = null,
    val excluding: List<String>? = null,
    var creatorUserInfoId: Long? = null,
)