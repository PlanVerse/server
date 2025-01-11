package com.planverse.server.team.dto

import jakarta.validation.constraints.NotBlank

data class TeamInfoUpdateRequestDTO(
    @field:NotBlank
    val teamId: Long,
    val name: String? = null,
    val description: String? = null,
    val invite: List<String>? = null,
)