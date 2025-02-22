package com.planverse.server.file.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.annotation.MyBatisResponse
import org.apache.ibatis.type.Alias

@MyBatisResponse
@Alias("FileCombInfoDTO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class FileCombInfoDTO(
    var id: Long? = null,
    var key: String? = null,
    var name: Long? = null,
    var path: Long? = null,

    var fileRelInfos: List<FileRelInfoDTO>? = null,
)