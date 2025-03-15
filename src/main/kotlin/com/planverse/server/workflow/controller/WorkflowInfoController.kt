package com.planverse.server.workflow.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.workflow.service.WorkflowInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workflow")
class WorkflowInfoController(
    private val workflowInfoService: WorkflowInfoService
) {
    @GetMapping("/content/{projectInfoId}")
    fun getWorkflowContent(userInfo: UserInfo, @PathVariable(required = true) projectInfoId: Long): BaseResponse<Any> {
        val res = workflowInfoService.getWorkflowContent(userInfo, projectInfoId)
        return BaseResponse.success(res)
    }
}