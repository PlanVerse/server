package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamMemberInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TeamMemberRepository : JpaRepository<TeamMemberInfoEntity, Long> {
    fun findByTeamInfoId(teamInfoId: Long): Optional<List<TeamMemberInfoEntity>>
    fun findByTeamInfoIdAndUserInfoIdAndCreator(teamInfoId: Long, userInfoId: Long, creator: Boolean): Optional<TeamMemberInfoEntity>
    fun findByTeamInfoIdAndCreator(teamInfoId: Long, creator: Boolean): Optional<List<TeamMemberInfoEntity>>
}