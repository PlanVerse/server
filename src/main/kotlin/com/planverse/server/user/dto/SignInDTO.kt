package com.planverse.server.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SignInDTO(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Pattern(regexp = "^[A-Z]+(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$", message = "영문 숫자 특수기호 조합 8자리 이상 20자리 이하여야 합니다.")
    val pwd: String,
)