package com.planverse.server.project.mapper

import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface ProjectInfoMapper {
    // inset

    // update

    // select
    fun selectProjectAndMemberAndTeamInfo(projectInfoId: Long): ProjectAndMemberAndTeamInfoDTO

    // delete
}