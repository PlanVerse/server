package com.planverse.server.step.repository

import com.planverse.server.step.entity.StepInfoEntity
import org.apache.ibatis.annotations.Mapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Mapper
@Repository
interface StepInfoRepository : JpaRepository<StepInfoEntity, Long> {
    fun existsByIdAndProjectInfoIdAndDeleteYn(id: Long, projectInfoId: Long, deleteYn: String): Boolean
    fun existsByProjectInfoIdAndNameAndDeleteYn(projectInfoId: Long, name: String, deleteYn: String): Boolean
    fun findByIdAndProjectInfoIdAndDeleteYn(id: Long, projectInfoId: Long, deleteYn: String): Optional<StepInfoEntity>
    fun findAllByProjectInfoIdAndDeleteYn(projectInfoId: Long, deleteYn: String): Optional<List<StepInfoEntity>>
    fun findAllByProjectInfoIdAndDeleteYnOrderBySort(projectInfoId: Long, deleteYn: String): Optional<List<StepInfoEntity>>
}