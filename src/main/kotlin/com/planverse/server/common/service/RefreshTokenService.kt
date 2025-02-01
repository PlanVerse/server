package com.planverse.server.common.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import org.apache.commons.lang3.time.DateUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class RefreshTokenService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    @Transactional
    fun putRefreshToken(accessToken: String, refreshToken: String) {
        // 리프레시 토큰 리스트에 Access Token 추가
        redisTemplate.opsForHash<String, String>().put(Constant.REFRESH_TOKEN_KEY, accessToken, refreshToken)
        // 만료 시간 : 6달
        redisTemplate.expire(Constant.REFRESH_TOKEN_KEY, DateUtils.addMonths(Date(), 6).time, TimeUnit.MILLISECONDS)
    }

    fun hasKeyRefreshToken(accessToken: String) = redisTemplate.opsForHash<String, String>().hasKey(Constant.REFRESH_TOKEN_KEY, accessToken)

    fun getRefreshToken(accessToken: String): String {
        return redisTemplate.opsForHash<String, String>().get(Constant.REFRESH_TOKEN_KEY, accessToken) ?: throw BaseException(StatusCode.EXPIRED_TOKEN_RE_LOGIN)
    }

    fun deleteRefreshToken(accessToken: String) {
        if (this.hasKeyRefreshToken(accessToken)) {
            redisTemplate.opsForHash<String, String>().delete(Constant.REFRESH_TOKEN_KEY, accessToken)
        }
    }
}