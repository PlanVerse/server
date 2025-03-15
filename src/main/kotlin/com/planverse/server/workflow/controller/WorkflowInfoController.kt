package com.planverse.server.workflow.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.workflow.dto.WorkFlowInfoDTO
import com.planverse.server.workflow.dto.WorkFlowInfoRequestDTO
import com.planverse.server.workflow.service.WorkflowInfoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/workflow")
class WorkflowInfoController(
    private val workflowInfoService: WorkflowInfoService
) {

    @GetMapping("/list/{projectInfoId}")
    fun getWorkflowList(userInfo: UserInfo, @PathVariable(required = true) projectInfoId: Long): BaseResponse<List<WorkFlowInfoDTO>> {
        val res = workflowInfoService.getWorkflowList(userInfo, projectInfoId)
        return BaseResponse.success(res)
    }

    @GetMapping("/content/{workflowInfoId}")
    fun getWorkflowContent(userInfo: UserInfo, @PathVariable(required = true) workflowInfoId: Long): BaseResponse<WorkFlowInfoDTO> {
        val res = workflowInfoService.getWorkflowContent(userInfo, workflowInfoId)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createWorkflowContent(userInfo: UserInfo, @RequestBody workFlowInfoRequestDTO: WorkFlowInfoRequestDTO): BaseResponse<Any> {
        val res = workflowInfoService.createWorkflowContent(userInfo, workFlowInfoRequestDTO)
        return BaseResponse.success(res)
    }
}