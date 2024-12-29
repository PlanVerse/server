package com.planverse.server.common.dto

data class Jwt(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
)