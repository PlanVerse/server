package com.planverse.server.common.util

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {
    companion object {
        fun getCurrentEmail(): String {
            return SecurityContextHolder.getContext().authentication?.name ?: throw BaseException(StatusCode.UNAUTHORIZED)
        }
    }
}