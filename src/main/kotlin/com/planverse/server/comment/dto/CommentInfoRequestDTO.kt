package com.planverse.server.comment.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.comment.entity.CommentInfoEntity
import jakarta.validation.constraints.NotEmpty
import org.apache.ibatis.type.Alias

@Alias("CommentInfoRequestDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CommentInfoRequestDTO (
    var id: Long? = null,
    var key: String? = null,
    var workflowInfoId: Long,
    @field:NotEmpty
    var content: String,
) {
    fun toEntity() = CommentInfoEntity(
        id = id,
        key = key,
        workflowInfoId = workflowInfoId,
        content = content,
    )
}