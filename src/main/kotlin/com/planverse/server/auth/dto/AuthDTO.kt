package com.planverse.server.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AuthDTO(
    @field:NotBlank
    val token: String,

    @field:Email(message = "이메일 형식을 맞춰주세요.")
    @field:NotBlank
    val email: String
)
