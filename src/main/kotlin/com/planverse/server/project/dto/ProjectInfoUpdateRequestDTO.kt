package com.planverse.server.project.dto

import jakarta.validation.constraints.NotBlank
import org.apache.ibatis.type.Alias

@Alias("ProjectInfoUpdateRequestDTO")
data class ProjectInfoUpdateRequestDTO(
    @field:NotBlank
    val projectInfoId: Long,
    val name: String? = null,
    val description: String? = null,
    val private: Boolean? = false,
    val invite: List<String>? = null,
    val exclude: List<String>? = null,
    var creatorUserInfoId: Long? = null,
)