package com.planverse.server.common.config.data

import com.planverse.server.common.config.property.RedisConfigProperty
import com.planverse.server.common.util.RedisUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisNode
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(RedisConfigProperty::class)
class RedisConfig(
    private val redisConfigProperty: RedisConfigProperty,
) {
    @Bean("lettuceConnectionFactory")
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val sentinelConfig = RedisSentinelConfiguration().apply {
            master(redisConfigProperty.sentinel.master)
            redisConfigProperty.sentinel.nodes.map {
                sentinel(RedisNode.fromString(it))
            }
            sentinelUsername = redisConfigProperty.sentinel.username
            sentinelPassword = RedisPassword.of(redisConfigProperty.sentinel.password)

            username = redisConfigProperty.username
            password = RedisPassword.of(redisConfigProperty.password)
            database = redisConfigProperty.database
        }

        return LettuceConnectionFactory(sentinelConfig)
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