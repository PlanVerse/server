package com.planverse.server.common.util

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.hours

@Component
object S3Util {
    private lateinit var s3: S3Client
    private lateinit var bucketName: String

    fun init(client: S3Client, bucket: String) {
        s3 = client
        bucketName = bucket
    }

    fun getObjectUrl(
        keyName: String,
    ): String {
        val url = runBlocking {
            internalGetObjectUrl(keyName = keyName)
        }

        return url
    }

    fun putObject(
        keyName: String,
        body: RequestBody,
        objectMetadata: Map<String, String>? = null
    ) {
        runBlocking {
            internalPutObject(keyName = keyName, body = body, objectMetadata = objectMetadata)
        }
    }

    private suspend fun internalGetObjectUrl(
        keyName: String,
    ): String {
        try {
            val request = GetObjectRequest {
                bucket = bucketName
                key = keyName
            }

            s3.use {
                val presignedRequest = it.presignGetObject(request, 24.hours)
                val url = presignedRequest.url.toString()
                return url
            }
        } catch (e: Exception) {
            throw BaseException(StatusCode.CANNOT_GET_FILE)
        }
    }

    private suspend fun internalPutObject(
        keyName: String,
        body: RequestBody,
        objectMetadata: Map<String, String>? = null
    ) {
        try {
            val request = PutObjectRequest {
                bucket = bucketName
                key = keyName
                metadata = objectMetadata
            }

            s3.use {
                val presignedRequest = it.presignPutObject(request, 12.hours)

                val putRequest = Request.Builder()
                    .url(presignedRequest.url.toString())
                    .apply {
                        presignedRequest.headers.forEach { key, values ->
                            header(key, values.joinToString(", "))
                        }
                    }.put(body)
                    .build()

                OkHttpClient().newCall(putRequest).execute()
            }
        } catch (e: Exception) {
            throw BaseException(StatusCode.CANNOT_PUT_FILE)
        }
    }
}
