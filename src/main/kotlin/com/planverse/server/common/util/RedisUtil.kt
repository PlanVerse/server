package com.planverse.server.common.util

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
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

    /**
     * TimeUnit이 달은 지원하지 않으므로 timeout * 30으로 계산
     */
    fun setWithExpiryMonth(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout * 30, TimeUnit.DAYS)
    }

    /**
     * TimeUnit이 년은 지원하지 않으므로 timeout * 365으로 계산
     */
    fun setWithExpiryYear(key: String, value: Any, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout * 365, TimeUnit.DAYS)
    }

    // 데이터 조회
    fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    // 데이터 조회 후 타입 변환
    fun getStringValue(key: String): String {
        return get(key)?.toString() ?: throw BaseException(StatusCode.NO_DATA)
    }

    fun getLongValue(key: String): Long {
        return get(key)?.toString()?.toLong() ?: throw BaseException(StatusCode.NO_DATA)
    }

    // 키 존재 확인
    fun has(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    // 데이터 삭제
    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    // ======================== hash ========================
    // hash table 조회
    fun getHash(hash: String, key: String): Any? {
        return redisTemplate.opsForHash<String, Any>().get(hash, key)
    }

    // hash table 추가
    fun putHash(hash: String, key: String, value: Any) {
        redisTemplate.opsForHash<String, Any>().put(hash, key, value)
    }

    // hash table 만료시간 지정
    fun setExpireHash(hash: String, timeout: Long, unit: TimeUnit) {
        redisTemplate.expire(hash, timeout, unit)
    }

    // hash table 키 존재 확인
    fun hashHasKey(hash: String, key: Any): Boolean {
        return redisTemplate.opsForHash<String, Any>().hasKey(hash, key)
    }

    // hash table 삭제
    fun deleteHash(hash: String, key: String) {
        redisTemplate.opsForHash<String, Any>().delete(hash, key)
    }

    // ======================== set ========================
    // set 추가
    fun isSetMember(set: String, value: Any): Boolean? {
        return redisTemplate.opsForSet().isMember(set, value)
    }

    // set 추가
    fun putSet(set: String, value: Any) {
        redisTemplate.opsForSet().add(set, value)
    }

    // set 만료시간 지정
    fun setExpireSet(set: String, timeout: Long, unit: TimeUnit) {
        redisTemplate.expire(set, timeout, unit)
    }
}