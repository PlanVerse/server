package com.planverse.server.workflow.repository

import com.planverse.server.workflow.entity.WorkflowInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WorkflowInfoRepository : JpaRepository<WorkflowInfoEntity, Long> {
    fun findByIdAndProjectInfoIdAndDeleteYn(id: Long, projectInfoId: Long, deleteYn: String): Optional<WorkflowInfoEntity>
    fun findAllByProjectInfoIdAndDeleteYn(projectInfoId: Long, deleteYn: String, pageable: Pageable): Slice<WorkflowInfoEntity>
}