package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamMemberInfoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TeamMemberInfoRepository : JpaRepository<TeamMemberInfoEntity, Long> {
    fun findByTeamInfoIdAndCreatorAndDeleteYn(teamInfoId: Long, creator: Boolean, deleteYn: String): Optional<TeamMemberInfoEntity>
    fun findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoId: Long, userInfoId: Long, creator: Boolean, deleteYn: String): Optional<TeamMemberInfoEntity>
    fun findAllByTeamInfoIdAndCreatorAndDeleteYn(teamInfoId: Long, creator: Boolean, deleteYn: String): Optional<List<TeamMemberInfoEntity>>
    fun findAllByUserInfoIdAndCreatorAndDeleteYn(userInfoId: Long, creator: Boolean, deleteYn: String, pageable: Pageable): Slice<TeamMemberInfoEntity>
    fun findAllByTeamInfoIdAndDeleteYn(teamInfoId: Long, deleteYn: String): Optional<List<TeamMemberInfoEntity>>
}