package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoUpdateRequestDTO(
    @field:NotNull
    @field:Min(1)
    var workflowInfoId: Long,
    @field:NotNull
    @field:Min(1)
    var projectInfoId: Long,
    var stepInfoId: Long? = null,
    var title: String? = null,
    var content: Map<String, Any>? = emptyMap(),

    var assignInfo: List<Long>? = null,
)
