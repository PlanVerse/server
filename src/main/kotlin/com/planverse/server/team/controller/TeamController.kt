package com.planverse.server.team.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.service.TeamService
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/team")
class TeamController(
    private val teamService: TeamService
) {
    @GetMapping("/info/{teamId}")
    fun getTeamInfo(userInfo: UserInfo, @PathVariable(required = true) teamId: Long): BaseResponse<TeamInfoDTO> {
        val res = teamService.getTeamInfo(userInfo, teamId)
        return BaseResponse.success(res)
    }

    @GetMapping("/list/creator")
    fun getTeamListCreator(userInfo: UserInfo, pageable: Pageable): BaseResponse<Slice<TeamInfoDTO>> {
        val res = teamService.getTeamListCreator(userInfo, pageable)
        return BaseResponse.success(res)
    }

    @GetMapping("/list/member")
    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): BaseResponse<Slice<TeamInfoDTO>> {
        val res = teamService.getTeamListMember(userInfo, pageable)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createTeam(userInfo: UserInfo, @RequestPart("body") teamInfoRequestDTO: TeamInfoRequestDTO): BaseResponse<Any> {
        teamService.createTeam(userInfo, teamInfoRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/info")
    fun modifyTeamInfo(userInfo: UserInfo, @RequestBody teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO): BaseResponse<Any> {
        teamService.modifyTeamInfo(userInfo, teamInfoUpdateRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/invite")
    fun inviteTeamMember(userInfo: UserInfo, @RequestBody teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO): BaseResponse<Any> {
        teamService.inviteTeamMember(userInfo, teamInfoUpdateRequestDTO)
        return BaseResponse.success()
    }

    @PutMapping("/image/{teamId}")
    fun modifyTeamImage(userInfo: UserInfo, @PathVariable(required = true) teamId: Long, @RequestPart("file") multipartFile: MultipartFile): BaseResponse<Any> {
        teamService.modifyTeamImage(userInfo, teamId, multipartFile)
        return BaseResponse.success()
    }
}