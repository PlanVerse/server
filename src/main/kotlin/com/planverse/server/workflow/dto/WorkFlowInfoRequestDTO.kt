package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoRequestDTO(
    var id: Long? = null,
    @field:NotBlank
    var projectInfoId: Long,
    @field:NotBlank
    var stepInfoId: Long,
    var key: String? = null,
    var title: String,
    var content: Map<String, Any>? = null
) {
    companion object {
        fun toDto(workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoRequestDTO {
            return WorkFlowInfoRequestDTO(
                workflowInfoEntity.id,
                workflowInfoEntity.projectInfoId,
                workflowInfoEntity.stepInfoId,
                workflowInfoEntity.key,
                workflowInfoEntity.title,
                workflowInfoEntity.content
            )
        }

        fun toEntity(workflowInfoDTO: WorkFlowInfoRequestDTO): WorkflowInfoEntity {
            return WorkflowInfoEntity(
                workflowInfoDTO.id,
                workflowInfoDTO.projectInfoId,
                workflowInfoDTO.stepInfoId,
                workflowInfoDTO.key,
                workflowInfoDTO.title,
                workflowInfoDTO.content
            )
        }
    }

    fun toEntity(): WorkflowInfoEntity {
        return WorkflowInfoEntity(
            id = id,
            projectInfoId = projectInfoId,
            stepInfoId = stepInfoId,
            key = key,
            title = title,
            content = content
        )
    }
}
