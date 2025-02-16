package com.planverse.server.file.mapper

import com.planverse.server.file.dto.FileCombInfoDTO
import com.planverse.server.file.dto.FileRelInfoDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface FileInfoMapper {
    // inset

    // update

    // select
    fun selectFileCombineInfo(fileRelInfoDTO: FileRelInfoDTO): FileCombInfoDTO

    // delete
}