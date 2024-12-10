package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SecurityException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // 1. Request Header에서 JWT 토큰 추출
        val token: String = resolveToken(request)

        try {
            // 2. validateToken으로 토큰 유효성 검사
            if (jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: SecurityException) {
            logger.info("Invalid JWT Token", e)
            request.setAttribute("exception", StatusCode.INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            logger.info("Invalid JWT Token", e)
            request.setAttribute("exception", StatusCode.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            logger.info("Expired JWT Token", e)
            request.setAttribute("exception", StatusCode.EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            logger.info("Unsupported JWT Token", e)
            request.setAttribute("exception", StatusCode.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            logger.info("JWT claims string is empty.", e)
            request.setAttribute("exception", StatusCode.INVALID_TOKEN)
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.startsWith("/auth") || request.servletPath.startsWith("/mail") || request.servletPath.startsWith("/actuator")
    }

    /**
     * Request Header에서 토큰 정보 추출
     */
    private fun resolveToken(request: HttpServletRequest): String {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7)
        } else {
            throw BaseException(StatusCode.UNAUTHORIZED)
        }
    }
}