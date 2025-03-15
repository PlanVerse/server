package com.planverse.server.assign.repository

import com.planverse.server.assign.entity.AssignInfoEntity
import org.apache.ibatis.annotations.Mapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface AssignInfoRepository : JpaRepository<AssignInfoEntity, Long>{
}