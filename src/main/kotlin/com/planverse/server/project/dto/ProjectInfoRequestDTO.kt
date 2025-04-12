package com.planverse.server.project.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.project.entity.ProjectInfoEntity
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProjectInfoRequestDTO(
    val id: Long? = null,
    val key: String? = null,
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val name: String,
    val description: String? = null,

    @field:NotNull
    @field:Min(1)
    val teamInfoId: Long,

    val invite: List<String>? = null,
) {
    fun toEntity(): ProjectInfoEntity {
        return ProjectInfoEntity(
            name = name,
            description = description,
            teamInfoId = teamInfoId
        )
    }
}