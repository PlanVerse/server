package com.planverse.server.file.repository

import com.planverse.server.file.entity.FileRelInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FileRelInfoRepository : JpaRepository<FileRelInfoEntity, Long> {
    fun findByTargetAndTargetIdAndDeleteYn(target: String, targetId: Long, deleteYn: String): Optional<FileRelInfoEntity>
    fun findAllByTargetAndTargetIdAndDeleteYn(target: String, targetId: Long, deleteYn: String): Optional<List<FileRelInfoEntity>>
}