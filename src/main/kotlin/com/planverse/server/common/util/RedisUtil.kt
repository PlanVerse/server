package com.planverse.server.common.util

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
object RedisUtil {

    private lateinit var redisTemplate: RedisTemplate<String, Any>

    fun setRedisTemplate(template: RedisTemplate<String, Any>) {
        redisTemplate = template
    }

    // 데이터 저장
    fun set(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    // 만료시간이 있는 데이터 저장
    fun setWithExpiryMs(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS)
    }

    fun setWithExpirySec(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS)
    }

    fun setWithExpiryMin(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES)
    }

    fun setWithExpiryHour(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.HOURS)
    }

    fun setWithExpiryDay(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.DAYS)
    }

    // 데이터 조회
    fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    // 키 존재 확인
    fun has(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    // 데이터 삭제
    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}