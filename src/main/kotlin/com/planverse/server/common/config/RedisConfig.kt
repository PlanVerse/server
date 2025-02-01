package com.planverse.server.common.config

import com.planverse.server.common.util.RedisUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

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
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(host, port)
        redisStandaloneConfiguration.password = RedisPassword.of(password)
        redisStandaloneConfiguration.database = database
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean("redisTemplate")
    fun redisTemplate(@Qualifier("lettuceConnectionFactory") lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = lettuceConnectionFactory
        redisTemplate.setEnableTransactionSupport(true)
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = StringRedisSerializer()

        RedisUtil.setRedisTemplate(redisTemplate)

        return redisTemplate
    }
}   