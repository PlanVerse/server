package com.planverse.server.team.dto

import jakarta.validation.constraints.NotBlank
import org.apache.ibatis.type.Alias

@Alias("TeamInfoUpdateRequestDTO")
data class TeamInfoUpdateRequestDTO(
    @field:NotBlank
    val teamId: Long,
    val name: String? = null,
    val description: String? = null,
    val private: Boolean? = false,
    val invite: List<String>? = null,
    val exclude: List<String>? = null,
    var creatorUserInfoId: Long? = null,
)