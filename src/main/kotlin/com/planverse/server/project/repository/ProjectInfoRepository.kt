package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectInfoRepository : JpaRepository<ProjectInfoEntity, Long>