package com.planverse.server.step.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.step.dto.StepInfoRequestDTO
import com.planverse.server.step.dto.StepInfoResponseDTO
import com.planverse.server.step.service.StepInfoService
import com.planverse.server.user.dto.UserInfo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/step")
class StepInfoController(
    private val stepInfoService: StepInfoService
) {
    @GetMapping("/list/{projectInfoId}")
    fun getStepInfoList(userInfo: UserInfo, @PathVariable(required = true) projectInfoId: Long): BaseResponse<List<StepInfoResponseDTO>> {
        val res = stepInfoService.getStepInfoList(userInfo, projectInfoId)
        return BaseResponse.success(res)
    }


    @PostMapping("/{projectInfoId}")
    fun createStepInfo(userInfo: UserInfo, @RequestBody(required = true) stepInfoRequestDTO: StepInfoRequestDTO): BaseResponse<Any> {
        val res = stepInfoService.createStepInfo(userInfo, stepInfoRequestDTO)
        return BaseResponse.success(res)
    }
}