package com.planverse.server.common.service

import com.planverse.server.common.constant.Constant
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class TokenBlacklistService(
    private val refreshTokenService: RefreshTokenService,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    @Transactional
    fun addTokenBlacklist(accessToken: String) {
        // 블랙리스트에 Access Token 추가
        redisTemplate.opsForSet().add(Constant.BLACKLIST_TOKEN_KEY, accessToken)
        // 만료 시간 : 7일
        redisTemplate.expire(Constant.BLACKLIST_TOKEN_KEY, 7, TimeUnit.DAYS)
    }

    @Transactional
    fun addTokenBlacklistAndRemoveRefreshToken(accessToken: String) {
        // 블랙리스트에 Access Token 추가
        redisTemplate.opsForSet().add(Constant.BLACKLIST_TOKEN_KEY, accessToken)
        // 만료 시간 : 7일
        redisTemplate.expire(Constant.BLACKLIST_TOKEN_KEY, 7, TimeUnit.DAYS)
        // 리프레시 토큰 제거
        refreshTokenService.deleteRefreshToken(accessToken)
    }

    fun isBlackToken(accessToken: String): Boolean {
        // 블랙리스트에서 Access Token 확인
        return redisTemplate.opsForSet().isMember(Constant.BLACKLIST_TOKEN_KEY, accessToken) ?: false
    }
}