package com.planverse.server.team.dto

import com.planverse.server.team.entity.TeamInfoEntity
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TeamInfoRequestDTO(
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val name: String,
    val description: String? = null,
    val private: Boolean? = false,
    val invite: List<String>? = null,
) {
    fun toEntity(): TeamInfoEntity {
        return TeamInfoEntity(
            name = name,
            description = description,
            private = private,
        )
    }
}