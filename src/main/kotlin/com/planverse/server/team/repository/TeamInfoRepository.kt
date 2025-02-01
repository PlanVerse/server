package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TeamInfoRepository : JpaRepository<TeamInfoEntity, Long>