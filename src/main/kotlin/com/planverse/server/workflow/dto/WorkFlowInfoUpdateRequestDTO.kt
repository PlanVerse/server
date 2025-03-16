package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoUpdateRequestDTO(
    @field:NotBlank
    var workflowInfoId: Long,
    @field:NotBlank
    var projectInfoId: Long,
    var stepInfoId: Long? = null,
    var title: String? = null,
    var content: List<Map<String, Any>>? = emptyList(),

    var assignInfo: List<Long>? = null,
)
