package com.planverse.server.workflow.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.workflow.dto.WorkFlowInfoRequestDTO
import com.planverse.server.workflow.dto.WorkFlowInfoResponseDTO
import com.planverse.server.workflow.dto.WorkFlowInfoUpdateRequestDTO
import com.planverse.server.workflow.service.WorkflowInfoService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/workflow")
class WorkflowInfoController(
    private val workflowInfoService: WorkflowInfoService
) {

    @GetMapping("/list/{projectInfoId}")
    fun getWorkflowList(userInfo: UserInfo, @PathVariable(required = true) projectInfoId: Long, pageable: Pageable): BaseResponse<Slice<WorkFlowInfoResponseDTO>> {
        val res = workflowInfoService.getWorkflowList(userInfo, projectInfoId, pageable)
        return BaseResponse.success(res)
    }

    @GetMapping("/content/{workflowInfoId}")
    fun getWorkflowContent(userInfo: UserInfo, @PathVariable(required = true) workflowInfoId: Long): BaseResponse<WorkFlowInfoResponseDTO> {
        val res = workflowInfoService.getWorkflowContent(userInfo, workflowInfoId)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createWorkflowContent(userInfo: UserInfo, @Validated @RequestBody workFlowInfoRequestDTO: WorkFlowInfoRequestDTO): BaseResponse<Any> {
        val res = workflowInfoService.createWorkflowContent(userInfo, workFlowInfoRequestDTO)
        return BaseResponse.success(res)
    }

    @PutMapping
    fun modifyWorkflowContent(userInfo: UserInfo, @Validated @RequestBody workFlowInfoUpdateRequestDTO: WorkFlowInfoUpdateRequestDTO): BaseResponse<Any> {
        val res = workflowInfoService.modifyWorkflowContent(userInfo, workFlowInfoUpdateRequestDTO)
        return BaseResponse.success(res)
    }
}