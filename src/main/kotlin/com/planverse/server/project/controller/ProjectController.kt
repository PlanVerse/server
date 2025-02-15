package com.planverse.server.project.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import com.planverse.server.project.dto.ProjectInfoRequestDTO
import com.planverse.server.project.service.ProjectService
import com.planverse.server.user.dto.UserInfo
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/project")
class ProjectController(
    private val projectService: ProjectService
) {
    @GetMapping("/info/{projectId}")
    fun getProjectInfo(userInfo: UserInfo, @PathVariable(required = true) projectId: Long): BaseResponse<ProjectAndMemberAndTeamInfoDTO> {
        val res = projectService.getProjectInfo(userInfo, projectId)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createProject(userInfo: UserInfo, @RequestPart("body") projectInfoRequestDTO: ProjectInfoRequestDTO, @RequestPart("file") multipartFile: MultipartFile?): BaseResponse<Any> {
        projectService.createProject(userInfo, projectInfoRequestDTO, multipartFile)
        return BaseResponse.success()
    }
}