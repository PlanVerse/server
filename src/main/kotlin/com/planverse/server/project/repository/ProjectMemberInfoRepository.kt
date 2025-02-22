package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectMemberInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProjectMemberInfoRepository : JpaRepository<ProjectMemberInfoEntity, Long> {
    fun findByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId: Long, userInfoId: Long, creator: Boolean, deleteYn: String): Optional<ProjectMemberInfoEntity>
}