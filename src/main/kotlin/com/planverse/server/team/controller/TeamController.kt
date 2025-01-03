package com.planverse.server.team.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.service.TeamService
import com.planverse.server.user.dto.UserInfo
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/team")
class TeamController(
    private val teamService: TeamService
) {
    @GetMapping("/info/{teamId}")
    fun getTeamInfo(userInfo: UserInfo, @PathVariable(required = true) teamId: Long): BaseResponse<*> {
        val res = teamService.getTeamInfo(userInfo, teamId)
        return BaseResponse.success(res)
    }

    @GetMapping("/list/creator")
    fun getTeamListCreator(
        userInfo: UserInfo,
        pageable: Pageable
    ): BaseResponse<Slice<TeamInfoDTO>> {
        val res = teamService.getTeamListCreator(userInfo, pageable)
        return BaseResponse.success(res)
    }

    @GetMapping("/list/member")
    fun getTeamListMember(
        userInfo: UserInfo,
        pageable: Pageable
    ): BaseResponse<Slice<TeamInfoDTO>> {
        val res = teamService.getTeamListMember(userInfo, pageable)
        return BaseResponse.success(res)
    }

    @PostMapping
    fun createTeam(userInfo: UserInfo, @RequestBody teamInfoRequestDTO: TeamInfoRequestDTO): BaseResponse<Any> {
        teamService.createTeam(userInfo, teamInfoRequestDTO)
        return BaseResponse.success()
    }
}