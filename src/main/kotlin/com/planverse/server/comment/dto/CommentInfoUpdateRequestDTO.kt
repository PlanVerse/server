package com.planverse.server.comment.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.comment.entity.CommentInfoEntity
import jakarta.validation.constraints.NotEmpty
import org.apache.ibatis.type.Alias

@Alias("CommentInfoUpdateRequestDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CommentInfoUpdateRequestDTO (
    var id: Long,
    var workflowInfoId: Long,
    @field:NotEmpty
    var content: String,
) {
    fun toEntity() = CommentInfoEntity(
        id = id,
        workflowInfoId = workflowInfoId,
        content = content,
    )
}