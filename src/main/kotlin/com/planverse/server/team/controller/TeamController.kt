package com.planverse.server.team.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.service.TeamService
import com.planverse.server.user.dto.UserInfo
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
    fun getTeamInfo(userInfo: UserInfo, @PathVariable teamId: Long): BaseResponse<*> {
        val res: TeamInfoDTO = teamService.getTeamInfo(userInfo, teamId)
        return BaseResponse.success(res)
    }

    @GetMapping("/list")
    fun getTeamList(): BaseResponse<Any> {
        return BaseResponse.success()
    }
}