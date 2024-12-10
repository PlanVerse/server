package com.planverse.server.user.dto

import com.planverse.server.user.entity.UserInfoEntity

data class UserInfoDTO(
    var id: Long? = null,
    var name: String,
    var nickname: String,
    var email: String,
    var password: String,
) {
    fun toEntity(): UserInfoEntity {
        return UserInfoEntity(
            id,
            name,
            nickname,
            email,
            password,
        )
    }

    companion object {
        fun toDto(userInfoEntity: UserInfoEntity): UserInfoDTO {
            return UserInfoDTO(
                userInfoEntity.id,
                userInfoEntity.name,
                userInfoEntity.nickname,
                userInfoEntity.email,
                userInfoEntity.password
            )
        }
    }
}