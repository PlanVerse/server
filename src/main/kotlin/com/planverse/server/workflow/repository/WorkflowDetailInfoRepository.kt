package com.planverse.server.workflow.repository

import com.planverse.server.workflow.entity.WorkflowDetailInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WorkflowDetailInfoRepository : JpaRepository<WorkflowDetailInfoEntity, Long> {
    fun findByWorkflowInfoId(workflowInfoId: Long): Optional<WorkflowDetailInfoEntity>
}