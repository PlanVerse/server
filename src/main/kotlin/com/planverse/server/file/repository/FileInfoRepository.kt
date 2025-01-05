package com.planverse.server.file.repository

import com.planverse.server.file.entity.FileInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FileInfoRepository : JpaRepository<FileInfoEntity, Long> {
}