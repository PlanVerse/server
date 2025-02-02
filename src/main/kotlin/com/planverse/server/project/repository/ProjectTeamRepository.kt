package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectTeamInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectTeamRepository : JpaRepository<ProjectTeamInfoEntity, Long>