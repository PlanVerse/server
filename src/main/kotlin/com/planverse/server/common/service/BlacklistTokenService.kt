package com.planverse.server.common.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.util.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class BlacklistTokenService(
    private val refreshTokenService: RefreshTokenService,
) {
    @Transactional
    fun addTokenToBlacklist(accessToken: String) {
        // 블랙리스트에 Access Token 추가
        RedisUtil.putSet(Constant.BLACKLIST_TOKEN_KEY, accessToken)
        // 만료 시간 : 7일
        RedisUtil.setExpireSet(Constant.BLACKLIST_TOKEN_KEY, 7, TimeUnit.DAYS)
    }

    @Transactional
    fun addTokenToBlacklistAndRemoveRefreshToken(accessToken: String) {
        this.addTokenToBlacklist(accessToken)
        // 리프레시 토큰 제거
        refreshTokenService.deleteRefreshToken(accessToken)
    }

    fun isBlockedToken(accessToken: String): Boolean {
        // 블랙리스트에서 Access Token 확인
        return RedisUtil.isSetMember(Constant.BLACKLIST_TOKEN_KEY, accessToken) ?: false
    }
}