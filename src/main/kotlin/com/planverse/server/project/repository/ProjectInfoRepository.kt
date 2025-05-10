package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProjectInfoRepository : JpaRepository<ProjectInfoEntity, Long> {
    fun existsByIdAndDeleteYn(projectInfoId: Long, deleteYn: String): Boolean
    fun findAllByTeamInfoIdAndDeleteYn(teamInfoId: Long, deleteYn: String, pageable: Pageable): Slice<ProjectInfoEntity>

    @Query("""
        SELECT
            pi
        FROM ProjectInfoEntity pi
        JOIN ProjectMemberInfoEntity pmi ON pmi.projectInfoId = pi.id AND pmi.userInfoId = :userInfoId AND pmi.deleteYn = 'N'
        WHERE pi.teamInfoId = :teamInfoId
          AND pi.deleteYn = 'N'
    """)
    fun findAllByTeamInfoIdAndUserInfoIdAndDeleteYnOnlyMember(teamInfoId: Long, userInfoId: Long, pageable: Pageable): Slice<ProjectInfoEntity>

    @Query("""
        SELECT
            pi
        FROM ProjectInfoEntity pi
        JOIN ProjectMemberInfoEntity pmi ON pmi.projectInfoId = pi.id AND pmi.userInfoId = :userInfoId AND pmi.deleteYn = 'N'
        WHERE pi.teamInfoId IN (:teamInfoIds)
          AND pi.deleteYn = 'N'
        ORDER BY pi.createdAt DESC
    """)
    fun findAllByTeamInfoIdsAndUserInfoIdAndDeleteYnOnlyMemberOrderByCreatedAtDesc(teamInfoIds: List<Long>, userInfoId: Long, pageable: Pageable): Slice<ProjectInfoEntity>
}