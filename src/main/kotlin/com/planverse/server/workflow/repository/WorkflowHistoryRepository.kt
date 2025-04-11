package com.planverse.server.workflow.repository

import com.planverse.server.workflow.entity.WorkflowHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WorkflowHistoryRepository : JpaRepository<WorkflowHistoryEntity, Long> {
}