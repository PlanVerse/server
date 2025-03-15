package com.planverse.server.assign.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.assign.entity.AssignInfoEntity

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class AssignInfoDTO(
    var id: Long? = null,
    var workflowInfoId: Long,
    var userInfoId: Long,
) {
    companion object {
        fun toDto(assignInfoEntity: AssignInfoEntity): AssignInfoDTO {
            return AssignInfoDTO(
                assignInfoEntity.id,
                assignInfoEntity.workflowInfoId,
                assignInfoEntity.userInfoId,
            )
        }

        fun toEntity(workflowInfoId: Long, userInfoId: Long): AssignInfoEntity {
            return AssignInfoEntity(
                workflowInfoId = workflowInfoId,
                userInfoId = userInfoId,
            )
        }
    }
}
