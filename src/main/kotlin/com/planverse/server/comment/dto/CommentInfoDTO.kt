package com.planverse.server.comment.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.comment.entity.CommentInfoEntity
import jakarta.validation.constraints.NotEmpty
import org.apache.ibatis.type.Alias
import java.time.LocalDateTime

@Alias("CommentInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CommentInfoDTO(
    var id: Long? = null,
    var key: String? = null,
    var workflowInfoId: Long? = null,
    @field:NotEmpty
    var content: String? = null,

    var name: String? = null,
    var email: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime? = null
) {
    companion object {
        fun toDTO(commentInfoEntity: CommentInfoEntity) = CommentInfoDTO(
            id = commentInfoEntity.id,
            key = commentInfoEntity.key,
            workflowInfoId = commentInfoEntity.workflowInfoId,
            content = commentInfoEntity.content
        )


        fun toDTO(commentInfoEntity: CommentInfoEntity, name: String, email: String) = CommentInfoDTO(
            id = commentInfoEntity.id,
            key = commentInfoEntity.key,
            workflowInfoId = commentInfoEntity.workflowInfoId,
            content = commentInfoEntity.content,
            name = name,
            email = email,
            createdAt = commentInfoEntity.createdAt,
        )
    }
}