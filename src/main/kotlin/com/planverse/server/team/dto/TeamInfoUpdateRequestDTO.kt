package com.planverse.server.team.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.apache.ibatis.type.Alias

@Alias("TeamInfoUpdateRequestDTO")
data class TeamInfoUpdateRequestDTO(
    @field:NotNull
    @field:Min(1)
    val teamId: Long,
    val name: String? = null,
    val description: String? = null,
    val private: Boolean? = false,
    val invite: List<String>? = null,
    val exclude: List<String>? = null,
    var creatorUserInfoId: Long? = null,
)