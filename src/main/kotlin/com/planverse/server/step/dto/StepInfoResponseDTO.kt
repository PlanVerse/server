package com.planverse.server.step.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.step.entity.StepInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StepInfoResponseDTO(
    var id: Long? = null,
    var projectInfoId: Long,
    var name: String,
    var sort: Int,
) {
    companion object {
        fun toDto(stepInfoEntity: StepInfoEntity): StepInfoResponseDTO {
            return StepInfoResponseDTO(
                stepInfoEntity.id,
                stepInfoEntity.projectInfoId,
                stepInfoEntity.name,
                stepInfoEntity.sort,
            )
        }
    }
}
