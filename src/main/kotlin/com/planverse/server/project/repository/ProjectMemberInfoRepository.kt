package com.planverse.server.project.repository

import com.planverse.server.project.entity.ProjectMemberInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectMemberInfoRepository : JpaRepository<ProjectMemberInfoEntity, Long>