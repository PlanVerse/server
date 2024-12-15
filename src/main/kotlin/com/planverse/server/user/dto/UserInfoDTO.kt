package com.planverse.server.user.dto

import com.planverse.server.user.entity.UserInfoEntity
import org.springframework.security.core.userdetails.UserDetails

data class UserInfoDTO(
    var id: Long? = null,
    var name: String,
    var nickname: String,
    var email: String,
    var pwd: String,
    var authentication: Boolean,
) {
    fun toEntity(): UserInfoEntity {
        return UserInfoEntity(
            id,
            name,
            nickname,
            email,
            pwd,
            authentication
        )
    }

    companion object {
        fun toDto(userInfoEntity: UserInfoEntity): UserInfoDTO {
            return UserInfoDTO(
                userInfoEntity.id,
                userInfoEntity.name,
                userInfoEntity.nickname,
                userInfoEntity.email,
                userInfoEntity.password,
                userInfoEntity.authentication
            )
        }
    }
}