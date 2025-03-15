package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectInfoRepository : JpaRepository<ProjectInfoEntity, Long> {
    fun existsByIdAndDeleteYn(projectInfoId: Long, deleteYn: String): Boolean
    fun findAllByTeamInfoIdAndDeleteYn(userInfoId: Long, deleteYn: String, pageable: Pageable): Slice<ProjectInfoEntity>
}