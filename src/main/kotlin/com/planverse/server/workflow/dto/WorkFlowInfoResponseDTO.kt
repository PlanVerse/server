package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.assign.dto.AssignInfoResponseDTO
import com.planverse.server.step.dto.StepInfoDTO
import com.planverse.server.workflow.entity.WorkflowInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoResponseDTO(
    var id: Long? = null,
    var key: String? = null,
    var projectInfoId: Long,
    var stepInfoId: Long,
    var title: String,
    var content: Map<String, Any>? = emptyMap(),

    var stepInfo: StepInfoDTO? = null,

    var assignInfo: List<AssignInfoResponseDTO>? = null,
) {
    companion object {
        fun toDto(workflowInfoEntity: WorkflowInfoEntity): WorkFlowInfoResponseDTO {
            return WorkFlowInfoResponseDTO(
                workflowInfoEntity.id,
                workflowInfoEntity.key,
                workflowInfoEntity.projectInfoId,
                workflowInfoEntity.stepInfoId,
                workflowInfoEntity.title,
                workflowInfoEntity.content
            )
        }
    }
}
