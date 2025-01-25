package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.Jwt
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.service.TokenBlacklistService
import com.planverse.server.common.util.RedisUtil
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
    val blacklistService: TokenBlacklistService,
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
        // 2시간
        val accessTokenExpr = DateUtils.addHours(now, 2)
        // 대략 1주일 :: 정확한 수치는 소수점이므로 올림 적용
        val refreshTokenExpr = DateUtils.addMonths(now, 6)

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(authentication.name)
            .claims(
                mutableMapOf<String, String>(
                    "auth" to authorities,
                )
            )
            .expiration(accessTokenExpr)
            .signWith(secretKey)
            .compact()

        val userInfoEntity = authentication.principal.let { principal -> principal as UserInfoEntity }
        RedisUtil.setWithExpiryHour(userInfoEntity.email, userInfoEntity.id!!.toString(), 2)

        // Refresh Token 생성
        val refreshToken = Jwts.builder()
            .subject(authentication.name)
            .expiration(refreshTokenExpr)
            .signWith(secretKey)
            .compact()

        RedisUtil.setWithExpiryMonth(
            accessToken,
            refreshToken,
            6
        )

        return Jwt(
            "Bearer",
            accessToken
        )
    }

    /**
     * refreshToken으로 JWT Token 갱신
     */
    fun generateTokenByRefreshToken(refreshToken: String): Jwt {
        val authentication: Authentication = this.getRefreshAuthentication(refreshToken)
        // 권한 가져오기
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        val now = Date()
        // 2시간
        val accessTokenExpr = DateUtils.addHours(now, 2)

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(authentication.name)
            .claims(
                mutableMapOf<String, String>(
                    "auth" to authorities,
                )
            )
            .expiration(accessTokenExpr)
            .signWith(secretKey)
            .compact()

        RedisUtil.setWithExpiryMonth(
            accessToken,
            refreshToken,
            6
        )

        return Jwt(
            "Bearer",
            accessToken
        )
    }

    /**
     * Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼냄
     */
    fun getAuthentication(accessToken: String): Authentication {
        // Jwt 토큰 복호화
        val claims = this.parseClaims(accessToken)

        if (claims["auth"] == null) {
            throw BaseException(StatusCode.UNAUTHORIZED)
        }

        // 클레임에서 권한 정보 가져오기
        val authorities: Collection<GrantedAuthority?> = Arrays.stream(claims["auth"].toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())

        // UserInfo 객체를 만들어서 Authentication return
        return UsernamePasswordAuthenticationToken(UserInfo.toDto(claims, accessToken), "", authorities)
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
    fun validateToken(accessToken: String): Boolean {
        if (blacklistService.isBlackToken(accessToken)) {
            throw BaseException(StatusCode.EXPIRED_TOKEN_RE_LOGIN)
        }

        try {
            this.parseClaims(accessToken)
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