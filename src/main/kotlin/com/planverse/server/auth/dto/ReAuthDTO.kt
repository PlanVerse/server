package com.planverse.server.auth.dto

import jakarta.validation.constraints.NotBlank

data class ReAuthDTO(
    @field:NotBlank
    val email: String
)
