package com.planverse.server.common.config

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.net.url.Url
import com.planverse.server.common.util.S3Util
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config(
    @Value("\${spring.aws.s3.accessKeyId}")
    private val accessKeyId: String,

    @Value("\${spring.aws.s3.secretAccessKey}")
    private val secretAccessKey: String,

    @Value("\${spring.aws.s3.bucket}")
    private val bucket: String,

    @Value("\${spring.aws.s3.endpoint}")
    private val endpoint: String,
) {
    @Bean(destroyMethod = "close")
    fun s3Client(): S3Client {
        val s3Client = S3Client {
            this.region = "auto"
            this.endpointUrl = Url.parse(endpoint)
            this.credentialsProvider = StaticCredentialsProvider(
                credentials = Credentials(
                    accessKeyId = accessKeyId,
                    secretAccessKey = secretAccessKey
                )
            )
        }.also { S3Util.init(it, bucket) }

        return s3Client
    }
}