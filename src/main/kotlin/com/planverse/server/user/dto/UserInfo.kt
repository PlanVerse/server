package com.planverse.server.user.dto

import io.jsonwebtoken.Claims

data class UserInfo(
    val id: Long,
    val email: String,
    val accessToken: String? = null,
) {
    companion object {
        fun toDto(claims: Claims): UserInfo {
            return UserInfo(
                claims["identity"].toString().toLong(),
                claims["sub"].toString(),
            )
        }

        fun toDto(claims: Claims, accessToken: String): UserInfo {
            return UserInfo(
                claims["identity"].toString().toLong(),
                claims["sub"].toString(),
                accessToken = accessToken
            )
        }
    }
}