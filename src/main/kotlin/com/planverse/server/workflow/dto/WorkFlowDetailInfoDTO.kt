package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.workflow.entity.WorkflowDetailInfoEntity
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowDetailInfoDTO(
    var id: Long? = null,
    var workflowInfoId: Long,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var startAt: LocalDateTime? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var endAt: LocalDateTime? = null,
) {
    companion object {
        fun toDto(workflowDetailInfoEntity: WorkflowDetailInfoEntity) = WorkFlowDetailInfoDTO(
            workflowDetailInfoEntity.id,
            workflowDetailInfoEntity.workflowInfoId,
            workflowDetailInfoEntity.startAt,
            workflowDetailInfoEntity.endAt,
        )

        fun toEntity(workFlowDetailInfoDTO: WorkFlowDetailInfoDTO) = WorkflowDetailInfoEntity(
            workFlowDetailInfoDTO.id,
            workFlowDetailInfoDTO.workflowInfoId,
            workFlowDetailInfoDTO.startAt,
            workFlowDetailInfoDTO.endAt,
        )
    }

    fun toEntity() = WorkflowDetailInfoEntity(
        id = id,
        workflowInfoId = workflowInfoId,
        startAt = startAt,
        endAt = endAt,
    )
}
