package com.planverse.server.common.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException


class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        // 1. Request Header에서 JWT 토큰 추출
        val token = resolveToken(request as HttpServletRequest)

        // 2. 토큰이 존재할경우 validateToken으로 토큰 유효성 검사
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            val res: HttpServletResponse = response as HttpServletResponse
            res.status = HttpServletResponse.SC_FORBIDDEN

            when (e) {
                is ExpiredJwtException -> {
                    val statusInfo = StatusCode.EXPIRED_TOKEN
                    res.status = statusInfo.httpStatus.value()
                    val refreshToken = RedisUtil.get(token!!) ?: setErrorResponse(response, statusInfo)
                    setErrorResponse(response, statusInfo, jwtTokenProvider.generateTokenByRefreshToken(refreshToken.toString()))
                }

                is SecurityException -> {
                    setErrorResponse(res, StatusCode.UNSUPPORTED_TOKEN)
                }

                is MalformedJwtException -> {
                    setErrorResponse(response, StatusCode.UNSUPPORTED_TOKEN)
                }

                is UnsupportedJwtException -> {
                    setErrorResponse(response, StatusCode.INVALID_TOKEN)
                }

                is SignatureException -> {
                    setErrorResponse(response, StatusCode.INVALID_TOKEN)
                }

                is IllegalArgumentException -> {
                    setErrorResponse(response, StatusCode.INVALID_TOKEN)
                }

                is BaseException -> {
                    setErrorResponse(response, e.status)
                }

                else -> {
                    setErrorResponse(response, StatusCode.FAIL)
                }
            }
        }
    }

    /**
     * Request Header에서 토큰 정보 추출
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    private fun setErrorResponse(
        response: ServletResponse,
        statusInfo: StatusCode,
        data: Any? = null
    ) {
        val objectMapper = ObjectMapper()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse = BaseResponse.error(status = statusInfo, data = data)
        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}