package com.planverse.server.workflow.repository

import com.planverse.server.workflow.entity.WorkflowHistoryEntity
import org.apache.ibatis.annotations.Mapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface WorkflowHistoryRepository : JpaRepository<WorkflowHistoryEntity, Long> {
}