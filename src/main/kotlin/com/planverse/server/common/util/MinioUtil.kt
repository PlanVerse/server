package com.planverse.server.common.util

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.http.Method
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.TimeUnit

@Component
object MinioUtil {
    private lateinit var minio: MinioClient
    private lateinit var bucketName: String

    fun init(client: MinioClient, bucket: String) {
        minio = client
        bucketName = bucket
    }

    fun getObjectUrl(
        keyName: String,
    ): String {
        val url = runBlocking {
            internalGetPresignedObjectUrl(keyName = keyName)
        }

        return url
    }

    fun putObject(
        keyName: String,
        multipartFile: MultipartFile,
        objectMetadata: Map<String, String>? = null
    ) {
        runBlocking {
            internalPutObject(keyName = keyName, multipartFile = multipartFile, objectMetadata = objectMetadata)
        }
    }

    private fun internalGetPresignedObjectUrl(
        keyName: String,
    ): String {
        try {
            val args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .`object`(keyName)
                .method(Method.GET)
                .expiry(24, TimeUnit.HOURS)
                .build()

            val url = minio.getPresignedObjectUrl(args)

            return url
        } catch (e: Exception) {
            throw BaseException(StatusCode.CANNOT_GET_FILE)
        }
    }

    private fun internalPutObject(
        keyName: String,
        multipartFile: MultipartFile,
        objectMetadata: Map<String, String>? = null
    ) {
        try {
            val args = PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(keyName)
                .stream(multipartFile.inputStream, multipartFile.size, -1)
                .userMetadata(objectMetadata)
                .build()

            minio.putObject(args)
        } catch (e: Exception) {
            throw BaseException(StatusCode.CANNOT_PUT_FILE)
        }
    }
}
