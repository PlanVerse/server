package com.planverse.server.team.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.team.service.TeamService
import com.planverse.server.user.dto.UserInfo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/team/")
class TeamController(
    private val teamService: TeamService
) {
    @GetMapping("/info/{teamId}")
    fun getTeamInfo(@AuthenticationPrincipal userInfo: UserInfo, @PathVariable teamId: Long): BaseResponse<Unit> {
        teamService.getTeamInfo(userInfo, teamId)
        return BaseResponse.success()
    }

    @GetMapping("/list")
    fun getTeamList(@AuthenticationPrincipal userInfo: UserInfo): BaseResponse<Unit> {
        return BaseResponse.success()
    }
}