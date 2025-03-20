package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.ObjectUtil
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        MDC.put("traceId", UUID.randomUUID().toString())

        // 1. Request Header에서 JWT 토큰 추출
        val token = resolveToken(request)

        // 2. 토큰이 존재할경우 validateToken으로 토큰 유효성 검사
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handleException(e, request, response, token)
        } finally {
            MDC.clear()
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

    private fun handleException(
        exception: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse,
        token: String? = null
    ) {
        try {
            response.contentType = MediaType.APPLICATION_JSON_VALUE

            // 로그아웃의 경우 자동으로 리프레시 토큰을 이용한 토큰 갱신은 이루어지면 안되기에 분기처리
            if (request.requestURI.endsWith("/sign-out")) {
                var baseResponse = BaseResponse.success(data = null)

                token?.let {
                    val statusInfo = StatusCode.EXPIRED_TOKEN_RE_LOGIN

                    response.status = statusInfo.httpStatus.value()
                    if (jwtTokenProvider.blacklistService.isBlockedToken(it)) {
                        baseResponse = BaseResponse.error(status = statusInfo)
                    } else {
                        response.status = StatusCode.SUCCESS.httpStatus.value()
                        jwtTokenProvider.blacklistService.addTokenToBlacklistAndRemoveRefreshToken(it)
                    }
                }

                response.writer.write(
                    ObjectUtil.convertObjectToString(baseResponse)
                )
            } else {
                var data: Any? = null
                val statusInfo = when (exception) {
                    is ExpiredJwtException -> {
                        token?.let {
                            data = jwtTokenProvider.regenerateTokenByAccessToken(it)
                        }

                        StatusCode.EXPIRED_TOKEN
                    }

                    is MalformedJwtException, is UnsupportedJwtException, is SignatureException, is IllegalArgumentException -> StatusCode.INVALID_TOKEN
                    is BaseException -> exception.status
                    else -> StatusCode.FAIL
                }

                response.status = statusInfo.httpStatus.value()
                response.writer.write(
                    ObjectUtil.convertObjectToString(
                        BaseResponse.error(
                            status = statusInfo,
                            data = data
                        )
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}