package com.planverse.server.file.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.util.S3Util
import com.planverse.server.file.entity.FileInfoEntity
import com.planverse.server.file.entity.FileRelInfoEntity
import com.planverse.server.file.repository.FileInfoRepository
import com.planverse.server.file.repository.FileRelInfoRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@Service
@Transactional(readOnly = true)
class FileService(
    private val fileInfoRepository: FileInfoRepository,
    private val fileRelInfoRepository: FileRelInfoRepository,
) {
    internal fun getObjectMetaData(
        originalFilename: String,
        multipartFile: MultipartFile
    ): MutableMap<String, String> {
        val fileNameUtf8 = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8)

        return mutableMapOf(
            "Content-Type" to multipartFile.contentType!!,
            "Content-Disposition" to "attachment; filename=$fileNameUtf8",
            "Content-Length" to multipartFile.inputStream.available().toString(),
        )
    }

    @Transactional
    fun fileSave(target: String, targetId: Long, multipartFile: MultipartFile) = runBlocking {
        if (!multipartFile.isEmpty) {
            val originalFilename = multipartFile.originalFilename!!
            val objectMetaData = getObjectMetaData(originalFilename, multipartFile)
            val requestBody = multipartFile.bytes.toRequestBody("application/octet-stream; charset=utf-8".toMediaTypeOrNull())

            val key = UUID.randomUUID().toString()
            val fileInfoId = fileInfoRepository.save(FileInfoEntity(key = key, name = originalFilename, path = "")).id
            fileRelInfoRepository.save(FileRelInfoEntity(target = target, targetId = targetId, fileInfoId = fileInfoId))

            S3Util.putObject("$target/$targetId/$key", requestBody, objectMetaData)
        }
    }

    fun getFile(target: String, targetId: Long): String? {
        return fileRelInfoRepository.findByTargetAndTargetIdAndDeleteYn(target, targetId, Constant.DEL_N).flatMap { fileRelInfo ->
            fileInfoRepository.findById(fileRelInfo.fileInfoId!!).map { fileInfo ->
                S3Util.getObjectUrl("$target/$targetId/${fileInfo.key}")
            }
        }.orElse(null)
    }

    fun getFiles(target: String, targetId: Long): List<String> {
        return buildList {
            fileRelInfoRepository.findAllByTargetAndTargetIdAndDeleteYn(target, targetId, Constant.DEL_N).ifPresent { fileRelInfos ->
                fileRelInfos.forEach {
                    fileInfoRepository.findById(it.fileInfoId!!).ifPresent { fileInfo ->
                        add(S3Util.getObjectUrl("$target/$targetId/${fileInfo.key}"))
                    }
                }
            }
        }
    }
}