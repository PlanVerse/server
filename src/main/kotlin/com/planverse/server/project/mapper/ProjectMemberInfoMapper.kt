package com.planverse.server.project.mapper

import com.planverse.server.project.dto.ProjectInfoUpdateRequestDTO
import com.planverse.server.project.dto.ProjectMemberInfoDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface ProjectMemberInfoMapper {
    // inset

    // update

    // select
    fun selectProjectMemberInfoForCreator(projectInfoUpdateRequestDTO: ProjectInfoUpdateRequestDTO): List<ProjectMemberInfoDTO>

    // delete
}