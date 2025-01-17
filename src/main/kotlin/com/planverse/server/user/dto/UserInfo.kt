package com.planverse.server.user.dto

import io.jsonwebtoken.Claims

data class UserInfo(
    var id: Long? = null,
    val email: String,
    val accessToken: String? = null,
) {
    companion object {
        fun toDto(claims: Claims, accessToken: String): UserInfo {
            return UserInfo(
                email = claims["sub"].toString(),
                accessToken = accessToken
            )
        }
    }
}