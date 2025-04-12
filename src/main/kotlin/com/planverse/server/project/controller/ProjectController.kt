package com.planverse.server.project.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.project.dto.ProjectAndMemberAndTeamInfoDTO
import com.planverse.server.project.dto.ProjectInfoRequestDTO
import com.planverse.server.project.dto.ProjectInfoUpdateRequestDTO
import com.planverse.server.project.service.ProjectService
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/project")
class ProjectController(
    private val projectService: ProjectService
) {
    @GetMapping("/info/{projectInfoId}")
    fun getProjectInfo(userInfo: UserInfo, @PathVariable(required = true) projectInfoId: Long): BaseResponse<ProjectAndMemberAndTeamInfoDTO> {
        val res = projectService.getProjectInfo(userInfo, projectInfoId)
        return BaseResponse.success(res)
    }

    @GetMapping("/list/{teamInfoId}")
    fun getProjectInfoList(userInfo: UserInfo, @PathVariable(required = true) teamInfoId: Long, pageable: Pageable): BaseResponse<Slice<ProjectAndMemberAndTeamInfoDTO>> {
        val res = projectService.getProjectInfoList(userInfo, teamInfoId, pageable)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createProject(userInfo: UserInfo, @Validated @RequestBody projectInfoRequestDTO: ProjectInfoRequestDTO): BaseResponse<Any> {
        projectService.createProject(userInfo, projectInfoRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/info")
    fun modifyProjectInfo(userInfo: UserInfo, @Validated @RequestBody projectInfoUpdateRequestDTO: ProjectInfoUpdateRequestDTO): BaseResponse<Any> {
        projectService.modifyProjectInfo(userInfo, projectInfoUpdateRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/invite")
    fun inviteProjectMember(userInfo: UserInfo, @Validated @RequestBody projectInfoUpdateRequestDTO: ProjectInfoUpdateRequestDTO): BaseResponse<Any> {
        projectService.inviteProjectMember(userInfo, projectInfoUpdateRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/image/{projectInfoId}")
    fun modifyProjectImage(userInfo: UserInfo, @Validated @PathVariable(required = true) projectInfoId: Long, @RequestPart("file") multipartFile: MultipartFile): BaseResponse<Any> {
        projectService.modifyProjectImage(userInfo, projectInfoId, multipartFile)
        return BaseResponse.success()
    }
}