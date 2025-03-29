package com.planverse.server.common.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class RefreshTokenService {
    @Transactional
    fun putRefreshToken(accessToken: String, refreshToken: String) {
        // 리프레시 토큰 리스트에 Access Token 추가
        RedisUtil.putHash(Constant.REFRESH_TOKEN_KEY, accessToken, refreshToken)
        // 만료 시간 : 6달
        RedisUtil.setExpireHash(Constant.REFRESH_TOKEN_KEY, DateUtils.addMonths(Date(), 6).time, TimeUnit.MILLISECONDS)
    }

    fun hasKeyRefreshToken(accessToken: String) = RedisUtil.hashHasKey(Constant.REFRESH_TOKEN_KEY, accessToken)

    fun getRefreshToken(accessToken: String): String {
        return RedisUtil.getHash(Constant.REFRESH_TOKEN_KEY, accessToken)?.toString() ?: throw BaseException(StatusCode.EXPIRED_TOKEN_RE_LOGIN)
    }

    fun deleteRefreshToken(accessToken: String) {
        if (this.hasKeyRefreshToken(accessToken)) {
            RedisUtil.deleteHash(Constant.REFRESH_TOKEN_KEY, accessToken)
        }
    }
}