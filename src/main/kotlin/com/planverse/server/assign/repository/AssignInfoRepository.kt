package com.planverse.server.assign.repository

import com.planverse.server.assign.entity.AssignInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AssignInfoRepository : JpaRepository<AssignInfoEntity, Long> {
    fun findAllByWorkflowInfoId(workflowInfoId: Long): Optional<List<AssignInfoEntity>>
}