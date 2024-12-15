package com.planverse.server.common.util

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {
    companion object {
        fun getCurrentEmail(): String {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication == null || authentication.name == null) {
                throw BaseException(StatusCode.UNAUTHORIZED)
            }
            return authentication.name
        }
    }
}