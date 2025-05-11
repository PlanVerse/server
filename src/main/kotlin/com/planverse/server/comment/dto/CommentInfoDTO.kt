package com.planverse.server.comment.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.comment.entity.CommentInfoEntity
import jakarta.validation.constraints.NotEmpty
import org.apache.ibatis.type.Alias

@Alias("CommentInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CommentInfoDTO(
    var id: Long? = null,
    var key: String? = null,
    var workflowInfoId: Long? = null,
    @field:NotEmpty
    var content: Map<String, Any>? = emptyMap(),
) {
    companion object {
        fun toDTO(commentInfoEntity: CommentInfoEntity) = CommentInfoDTO(
            id = commentInfoEntity.id,
            key = commentInfoEntity.key,
            workflowInfoId = commentInfoEntity.workflowInfoId,
            content = commentInfoEntity.content
        )
    }
}