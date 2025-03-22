package com.planverse.server.step.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.step.entity.StepInfoEntity
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StepInfoRequestDTO(
    var id: Long? = null,
    @field:NotBlank
    var projectInfoId: Long,
    @field:NotBlank
    var name: String,
    @field:NotBlank
    var color: String,
    @field:NotBlank
    var sort: Int,
) {
    companion object {
        fun toDto(stepInfoEntity: StepInfoEntity): StepInfoRequestDTO {
            return StepInfoRequestDTO(
                stepInfoEntity.id,
                stepInfoEntity.projectInfoId,
                stepInfoEntity.name,
                stepInfoEntity.color,
                stepInfoEntity.sort,
            )
        }
    }

    fun toEntity(): StepInfoEntity {
        return StepInfoEntity(
            id = id,
            projectInfoId = projectInfoId,
            name = name,
            color = color,
            sort = sort,
        )
    }
}
