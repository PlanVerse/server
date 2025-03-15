package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoRequestDTO(
    var id: Long? = null,
    var key: String? = null,
    @field:NotBlank
    var projectInfoId: Long,
    @field:NotBlank
    var stepInfoId: Long,
    @field:NotBlank
    var title: String,
    @field:NotBlank
    var content: Map<String, Any>? = null,

    @field:NotBlank
    var assign: List<Long>? = null,
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
