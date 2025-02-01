package com.planverse.server.team.mapper

import com.planverse.server.user.dto.UserInfo
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface TeamInfoMapper {
    fun selectTeamInfoAndMemberList(userInfo: UserInfo)
}