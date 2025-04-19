package com.planverse.server.common.listner

import com.planverse.server.common.util.BeanUtil
import com.planverse.server.workflow.entity.WorkflowHistoryEntity
import com.planverse.server.workflow.entity.WorkflowInfoEntity
import com.planverse.server.workflow.repository.WorkflowHistoryRepository
import jakarta.persistence.PostPersist
import jakarta.persistence.PreUpdate

class WorkflowInfoListener {
    @PostPersist
    @PreUpdate
    fun preUpdate(before: WorkflowInfoEntity) {
        val workflowHistoryRepository = BeanUtil.getBean(WorkflowHistoryRepository::class.java)
        // 기존 데이터를 기반으로 WorkflowHistory 객체 생성
        val history = WorkflowHistoryEntity(
            workflowInfoId = before.id!!,
            key = before.key,
            projectInfoId = before.projectInfoId,
            stepInfoId = before.stepInfoId,
            title = before.title,
            content = before.content,
        ).apply {
            deleteYn = before.deleteYn
            createdBy = before.createdBy
            createdAt = before.createdAt
            updatedBy = before.updatedBy
            updatedAt = before.updatedAt
        }

        workflowHistoryRepository.save(history)
    }
}