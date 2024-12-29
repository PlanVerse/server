package com.planverse.server.user.dto

import io.jsonwebtoken.Claims

data class UserInfo(
    var id: Long,
    var email: String,
) {
    companion object {
        fun toDto(claims: Claims): UserInfo {
            return UserInfo(
                claims["identity"].toString().toLong(),
                claims["sub"].toString(),
            )
        }
    }
}