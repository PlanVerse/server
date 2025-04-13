package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoRequestDTO(
    var id: Long? = null,
    var key: String? = null,
    @field:NotNull
    @field:Min(1)
    var projectInfoId: Long,
    @field:NotNull
    @field:Min(1)
    var stepInfoId: Long,
    @field:NotBlank
    @field:Size(max = 500)
    var title: String,
    @field:NotNull
    var content: Map<String, Any>? = emptyMap(),

    var assignInfo: List<Long>? = null,
) {
    companion object {
        fun toDto(workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoRequestDTO {
            return WorkFlowInfoRequestDTO(
                workflowInfoEntity.id,
                workflowInfoEntity.key,
                workflowInfoEntity.projectInfoId,
                workflowInfoEntity.stepInfoId,
                workflowInfoEntity.title,
                workflowInfoEntity.content
            )
        }

        fun toEntity(workflowInfoDTO: WorkFlowInfoRequestDTO): WorkflowInfoEntity {
            return WorkflowInfoEntity(
                workflowInfoDTO.id,
                workflowInfoDTO.key,
                workflowInfoDTO.projectInfoId,
                workflowInfoDTO.stepInfoId,
                workflowInfoDTO.title,
                workflowInfoDTO.content
            )
        }
    }

    fun toEntity(): WorkflowInfoEntity {
        return WorkflowInfoEntity(
            id = id,
            key = key,
            projectInfoId = projectInfoId,
            stepInfoId = stepInfoId,
            title = title,
            content = content
        )
    }
}
