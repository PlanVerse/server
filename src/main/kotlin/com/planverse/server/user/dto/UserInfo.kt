package com.planverse.server.user.dto

import com.planverse.server.user.entity.UserInfoEntity
import io.jsonwebtoken.Claims

data class UserInfo(
    var id: Long? = null,
    val key: String,
    val email: String,
    val accessToken: String? = null,
) {
    companion object {
        fun toDto(claims: Claims, accessToken: String, email: String) = UserInfo(
            key = claims["sub"].toString(),
            email = email,
            accessToken = accessToken,
        )

        fun toDto(userInfoEntity: UserInfoEntity, accessToken: String) = UserInfo(
            key = userInfoEntity.key!!,
            email = userInfoEntity.email,
            accessToken = accessToken,
        )
    }
}