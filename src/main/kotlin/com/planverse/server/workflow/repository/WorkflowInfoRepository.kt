package com.planverse.server.workflow.repository

import com.planverse.server.workflow.entity.WorkflowInfoEntity
import org.apache.ibatis.annotations.Mapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Mapper
@Repository
interface WorkflowInfoRepository : JpaRepository<WorkflowInfoEntity, Long> {
    fun findByProjectInfoId(projectInfoId: Long): Optional<List<WorkflowInfoEntity>>
}