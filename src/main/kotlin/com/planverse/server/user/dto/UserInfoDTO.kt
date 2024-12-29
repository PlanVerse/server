package com.planverse.server.user.dto

import com.planverse.server.common.constant.SystemRole
import com.planverse.server.user.entity.UserInfoEntity

data class UserInfoDTO(
    var id: Long? = null,
    var key: String,
    var name: String,
    var nickname: String,
    var email: String,
    var pwd: String,
    var authentication: Boolean,
    var role: SystemRole,
) {
    companion object {
        fun toDto(userInfoEntity: UserInfoEntity): UserInfoDTO {
            return UserInfoDTO(
                userInfoEntity.id,
                userInfoEntity.key,
                userInfoEntity.name,
                userInfoEntity.nickname,
                userInfoEntity.email,
                userInfoEntity.password,
                userInfoEntity.authentication,
                userInfoEntity.role
            )
        }
    }
}