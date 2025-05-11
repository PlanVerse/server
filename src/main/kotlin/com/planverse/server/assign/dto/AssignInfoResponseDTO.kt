package com.planverse.server.assign.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.assign.entity.AssignInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class AssignInfoResponseDTO(
    var id: Long? = null,
    var workflowInfoId: Long,
    var userInfoId: Long,

    var username: String? = null,
    var email: String? = null,
) {
    companion object {
        fun toDto(assignInfoEntity: AssignInfoEntity) = AssignInfoResponseDTO(
            assignInfoEntity.id,
            assignInfoEntity.workflowInfoId,
            assignInfoEntity.userInfoId,
        )

        fun toDto(assignInfoEntity: AssignInfoEntity, username: String, email: String) = AssignInfoResponseDTO(
            assignInfoEntity.id,
            assignInfoEntity.workflowInfoId,
            assignInfoEntity.userInfoId,
            username = username,
            email = email
        )

        fun toEntity(workflowInfoId: Long, userInfoId: Long) = AssignInfoEntity(
            workflowInfoId = workflowInfoId,
            userInfoId = userInfoId,
        )
    }
}
