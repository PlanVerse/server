package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.Jwt
import com.planverse.server.common.exception.BaseException
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.entity.UserInfoEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider(
    val userDetailsService: UserDetailsService,
) {
    @Value("\${spring.jwt.secret}")
    private lateinit var secret: String
    private val secretKey: SecretKeySpec by lazy { SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().algorithm) }

    /**
     * User 정보로 JWT Token 생성
     */
    fun generateToken(authentication: Authentication): Jwt {
        // 권한 가져오기
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        val now = Date()
        // 30분
        val accessTokenExpr = DateUtils.addMinutes(now, 30)
        // 대략 1주일 :: 정확한 수치는 소수점이므로 올림 적용
        val refreshTokenExpr = DateUtils.addWeeks(now, 1)

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(authentication.name)
            .claims(
                mutableMapOf<String, String>(
                    "auth" to authorities,
                    "identity" to authentication.principal.let { principal -> principal as UserInfoEntity }.id!!.toString()
                )
            )
            .expiration(accessTokenExpr)
            .signWith(secretKey)
            .compact()

        // Refresh Token 생성
        val refreshToken = Jwts.builder()
            .subject(authentication.name)
            .expiration(refreshTokenExpr)
            .signWith(secretKey)
            .compact()

        return Jwt(
            "Bearer",
            accessToken,
            refreshToken
        )
    }

    /**
     * Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼냄
     */
    fun getAuthentication(accessToken: String): Authentication {
        // Jwt 토큰 복호화
        val claims = this.parseClaims(accessToken)

        if (claims["auth"] === null) {
            throw BaseException(StatusCode.NO_AUTHORITY)
        }

        // 클레임에서 권한 정보 가져오기
        val authorities: Collection<GrantedAuthority?> = Arrays.stream(claims["auth"].toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())

        // UserInfo 객체를 만들어서 Authentication return
        return UsernamePasswordAuthenticationToken(UserInfo.toDto(claims), "", authorities)
    }

    /**
     * 리프레시 토큰으로부터 정보를 꺼냄
     */
    fun getRefreshAuthentication(refreshToken: String): Authentication {
        try {
            val claims: Claims = this.parseClaims(refreshToken)
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(claims.subject)
            return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        } catch (e: Exception) {
            throw BaseException(StatusCode.INVALID_TOKEN)
        }
    }

    /**
     * 토큰 정보를 검증하는
     */
    fun validateToken(token: String): Boolean {
        try {
            this.parseClaims(token)
            return true
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * accessToken 복호화
     */
    fun parseClaims(accessToken: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(accessToken)
            .payload
    }
}