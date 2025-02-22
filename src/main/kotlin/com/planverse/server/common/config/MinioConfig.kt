package com.planverse.server.common.config

import com.planverse.server.common.util.MinioUtil
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    @Value("\${spring.minio.accessKeyId}")
    private val accessKeyId: String,

    @Value("\${spring.minio.secretAccessKey}")
    private val secretAccessKey: String,

    @Value("\${spring.minio.bucket}")
    private val bucket: String,

    @Value("\${spring.minio.endpoint}")
    private val endpoint: String,
) {
    @Bean(destroyMethod = "close")
    fun minioClient(): MinioClient {
        val minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKeyId, secretAccessKey)
            .build().also {
                MinioUtil.init(it, bucket)
            }

        return minioClient
    }
}