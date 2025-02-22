package com.planverse.server.file.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.apache.ibatis.type.Alias

@Alias("FileRelInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class FileRelInfoDTO(
    var id: Long? = null,
    var target: String? = null,
    var targetId: Long? = null,
    var fileInfoId: Long? = null,
)