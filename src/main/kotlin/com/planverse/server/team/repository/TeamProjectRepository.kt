package com.planverse.server.team.repository

import com.planverse.server.team.entity.TeamProjectInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TeamProjectRepository : JpaRepository<TeamProjectInfoEntity, Long>