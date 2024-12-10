package com.planverse.server.user.repository

import com.planverse.server.user.entity.UserInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserInfoRepository : JpaRepository<UserInfoEntity, Long> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Optional<UserInfoEntity>
}
