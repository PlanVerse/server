package com.planverse.server.workflow.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class WorkFlowInfoUpdateRequestDTO(
    @field:NotBlank
    var id: Long,
    var key: String? = null,
    @field:NotBlank
    var projectInfoId: Long,
    var stepInfoId: Long? = null,
    var title: String? = null,
    var content: Map<String, Any>? = null,

    var assignInfo: List<Long>? = null,
)
