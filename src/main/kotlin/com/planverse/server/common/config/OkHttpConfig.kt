package com.planverse.server.common.config

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class OkHttpConfig {
    @Bean("okHttpClient")
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().apply {
            this.connectTimeout(10, TimeUnit.SECONDS)
            this.writeTimeout(10, TimeUnit.SECONDS)
            this.readTimeout(10, TimeUnit.SECONDS)
        }.build()
    }
}