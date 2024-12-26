package com.planverse.server.user.dto

import jakarta.validation.constraints.NotBlank

data class ReAuthDTO(
    @field:NotBlank
    val email: String
)
