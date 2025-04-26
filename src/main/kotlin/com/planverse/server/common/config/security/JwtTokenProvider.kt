package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.constant.SystemRole
import com.planverse.server.common.dto.Jwt
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.service.BlacklistTokenService
import com.planverse.server.common.service.RefreshTokenService
import com.planverse.server.common.util.DateUtil
import com.planverse.server.common.util.RedisUtil
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.entity.UserInfoEntity
import com.planverse.server.user.service.UserDetailService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import javax.crypto.spec.SecretKeySpec
import kotlin.math.absoluteValue

@Component
class JwtTokenProvider(
    val userDetailService: UserDetailService,
    val blacklistService: BlacklistTokenService,
    val refreshTokenService: RefreshTokenService,
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

        // 유저 정보 가져오기
        val userInfoEntity = authentication.principal.let { principal -> principal as UserInfoEntity }

        val now = Date()
        val accessTokenExpr = if (authorities.equals(SystemRole.ROLE_SUPER_ADMIN.name, ignoreCase = true)) {
            RedisUtil.setWithExpiryYear(userInfoEntity.key!!, userInfoEntity.id!!.toString(), 10)

            // 10년
            DateUtils.addYears(now, 10)
        } else if (authorities.equals(SystemRole.ROLE_ADMIN.name, ignoreCase = true)) {
            RedisUtil.setWithExpiryHour(userInfoEntity.key!!, userInfoEntity.id!!.toString(), 4)

            // 4시간
            DateUtils.addHours(now, 4)
        } else {
            RedisUtil.setWithExpiryHour(userInfoEntity.key!!, userInfoEntity.id!!.toString(), 2)

            // 2시간
            DateUtils.addHours(now, 2)
        }

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(userInfoEntity.key)
            .claims(
                mutableMapOf<String, String>(
                    "auth" to authorities,
                )
            )
            .expiration(accessTokenExpr)
            .signWith(secretKey)
            .compact()

        // 6달
        val refreshTokenExpr = DateUtils.addMonths(now, 6)

        // Refresh Token 생성
        val refreshToken = Jwts.builder()
            .subject(userInfoEntity.key)
            .expiration(refreshTokenExpr)
            .signWith(secretKey)
            .compact()

        refreshTokenService.putRefreshToken(accessToken, refreshToken)

        return Jwt(
            "Bearer",
            accessToken,
            DateUtil.diffFromNowInMilliseconds(accessTokenExpr).absoluteValue,
        )
    }

    /**
     * refreshToken으로 JWT Token 갱신
     */
    private fun regenerateTokenByRefreshToken(refreshToken: String): Jwt {
        val authentication: Authentication = this.getRefreshAuthentication(refreshToken)

        // 권한 가져오기
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        val now = Date()
        // 2시간
        val accessTokenExpr = DateUtils.addHours(now, 2)

        val userInfoEntity = authentication.principal.let { principal -> principal as UserInfoEntity }

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(userInfoEntity.key)
            .claims(
                mutableMapOf<String, String>(
                    "auth" to authorities,
                )
            )
            .expiration(accessTokenExpr)
            .signWith(secretKey)
            .compact()

        refreshTokenService.putRefreshToken(accessToken, refreshToken)

        return Jwt(
            "Bearer",
            accessToken
        )
    }

    /**
     * accessToken으로 JWT Token 갱신
     */
    fun regenerateTokenByAccessToken(accessToken: String): Jwt? {
        if (!blacklistService.isBlockedToken(accessToken)) {
            val refreshToken = refreshTokenService.getRefreshToken(accessToken)
            blacklistService.addTokenToBlacklistAndRemoveRefreshToken(accessToken)
            return this.regenerateTokenByRefreshToken(refreshToken)
        } else {
            return null
        }
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
        val userInfoEntity = userDetailService.loadUserInfoBySubject(claims["sub"].toString())
        return UsernamePasswordAuthenticationToken(UserInfo.toDto(userInfoEntity, accessToken), "", authorities)
    }

    /**
     * 리프레시 토큰으로부터 정보를 꺼냄
     */
    fun getRefreshAuthentication(refreshToken: String): Authentication {
        try {
            val claims: Claims = this.parseClaims(refreshToken)
            val userDetails: UserDetails = userDetailService.loadUserDetailsBySubject(claims.subject)
            return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        } catch (e: Exception) {
            throw BaseException(StatusCode.INVALID_TOKEN)
        }
    }

    /**
     * 토큰 정보를 검증하는
     */
    fun validateToken(accessToken: String): Boolean {
        if (blacklistService.isBlockedToken(accessToken)) {
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