package com.planverse.server.common.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.url}")
    val host: String,

    @Value("\${spring.data.redis.port}")
    val port: Int,

    @Value("\${spring.data.redis.password}")
    val password: String,

    @Value("\${spring.data.redis.database}")
    val database: Int,
) {

    @Bean("lettuceConnectionFactory")
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ZERO)
            .shutdownTimeout(Duration.ZERO)
            .build()
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(host, port)
        redisStandaloneConfiguration.password = RedisPassword.of(password)
        redisStandaloneConfiguration.database = database
        LettuceConnectionFactory(host, port)
        return LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration)
    }

    @Bean
    fun redisTemplate(@Qualifier("lettuceConnectionFactory") redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = redisConnectionFactory
            this.isEnableDefaultSerializer = true
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = GenericJackson2JsonRedisSerializer()
            this.hashKeySerializer = StringRedisSerializer()
            this.hashValueSerializer = StringRedisSerializer()
        }
    }

    @Bean
    fun cacheManager(): CacheManager {
        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())) // Serialize 설정
            .entryTtl(Duration.ofMinutes(2)) // 캐시 기본 TTL 2분
            .disableCachingNullValues() // 캐싱 NULL 제외
        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(lettuceConnectionFactory())
            .cacheDefaults(configuration)
            .build()
    }
}