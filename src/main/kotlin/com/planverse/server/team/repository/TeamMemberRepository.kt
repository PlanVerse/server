package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamMemberInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TeamMemberRepository : JpaRepository<TeamMemberInfoEntity, Long> {
    fun findByTeamInfoIdAndUserInfoIdAndCreator(teamInfoId: Long, userInfoId: Long, creator: Boolean): Optional<TeamMemberInfoEntity>
    fun findByTeamInfoIdAndCreator(teamInfoId: Long, creator: Boolean): Optional<List<TeamMemberInfoEntity>>

    fun findAllByUserInfoIdAndCreator(userInfoId: Long, creator: Boolean, pageable: Pageable): Slice<TeamMemberInfoEntity>
}