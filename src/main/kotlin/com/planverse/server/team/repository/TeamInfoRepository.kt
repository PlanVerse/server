package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TeamInfoRepository : JpaRepository<TeamInfoEntity, Long> {
    fun findByIdAndCreatedBy(id: Long, createdBy: Long): Optional<TeamInfoEntity>
}