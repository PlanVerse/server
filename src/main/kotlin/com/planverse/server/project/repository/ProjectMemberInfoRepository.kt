package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectMemberInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ProjectMemberInfoRepository : JpaRepository<ProjectMemberInfoEntity, Long> {
    fun existsByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId: Long, userInfoId: Long, creator: Boolean, deleteYn: String): Boolean
    fun existsByProjectInfoIdAndUserInfoIdAndDeleteYn(projectInfoId: Long, userInfoId: Long, deleteYn: String): Boolean
    fun findByProjectInfoIdAndUserInfoIdAndCreatorAndDeleteYn(projectInfoId: Long, userInfoId: Long, creator: Boolean, deleteYn: String): Optional<ProjectMemberInfoEntity>
    fun findByProjectInfoId(projectInfoId: Long): Optional<List<ProjectMemberInfoEntity>>
}