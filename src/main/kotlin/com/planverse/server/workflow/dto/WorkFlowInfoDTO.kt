package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.workflow.entity.WorkflowInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoDTO(
    var id: Long? = null,
    var key: String? = null,
    var projectInfoId: Long,
    var stepInfoId: Long,
    var title: String,
    var content: Map<String, Any>? = emptyMap()
) {
    companion object {
        fun toDto(workflowInfoEntity: WorkflowInfoEntity) = WorkFlowInfoDTO(
            workflowInfoEntity.id,
            workflowInfoEntity.key,
            workflowInfoEntity.projectInfoId,
            workflowInfoEntity.stepInfoId,
            workflowInfoEntity.title,
            workflowInfoEntity.content
        )

        fun toEntity(workflowInfoDTO: WorkFlowInfoDTO) = WorkflowInfoEntity(
            workflowInfoDTO.id,
            workflowInfoDTO.key,
            workflowInfoDTO.projectInfoId,
            workflowInfoDTO.stepInfoId,
            workflowInfoDTO.title,
            workflowInfoDTO.content
        )
    }

    fun toEntity() = WorkflowInfoEntity(
        id = id,
        key = key,
        projectInfoId = projectInfoId,
        stepInfoId = stepInfoId,
        title = title,
        content = content
    )
}
