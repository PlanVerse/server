package com.planverse.server.workflow.service

import com.planverse.server.user.dto.UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkflowInfoService {
    fun getWorkflowContent(userInfo: UserInfo, projectInfoId: Long) {
    }
}