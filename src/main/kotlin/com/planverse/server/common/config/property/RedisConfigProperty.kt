package com.planverse.server.common.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@EnableConfigurationProperties(RedisSentinelProperties::class)
class RedisConfigProperty {
    lateinit var username: String
    lateinit var password: String
    var database: Int = 0
    lateinit var sentinel: RedisSentinelProperties
}

@ConfigurationProperties(prefix = "spring.data.redis.sentinel")
data class RedisSentinelProperties @ConstructorBinding constructor(
    val master: String,
    val nodes: List<String>
)
