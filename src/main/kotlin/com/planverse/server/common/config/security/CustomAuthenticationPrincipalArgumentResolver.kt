package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import com.planverse.server.user.dto.UserInfo
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CustomAuthenticationPrincipalArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserInfo::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        return when (val principal = authentication.principal) {
            is UserInfo -> {
                if (RedisUtil.has(principal.email)) {
                    principal.id = RedisUtil.getLongValue(principal.email)
                } else {
                    throw BaseException(StatusCode.USER_NOT_FOUND)
                }
                return principal
            }

            else -> null
        }
    }
}