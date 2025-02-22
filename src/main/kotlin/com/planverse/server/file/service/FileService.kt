package com.planverse.server.file.service

import com.planverse.server.common.annotation.Except
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.MinioUtil
import com.planverse.server.file.dto.FileCombInfoDTO
import com.planverse.server.file.dto.FileRelInfoDTO
import com.planverse.server.file.entity.FileInfoEntity
import com.planverse.server.file.entity.FileRelInfoEntity
import com.planverse.server.file.mapper.FileInfoMapper
import com.planverse.server.file.repository.FileInfoRepository
import com.planverse.server.file.repository.FileRelInfoRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Except
@Service
@Transactional(readOnly = true)
class FileService(
    private val fileInfoRepository: FileInfoRepository,
    private val fileRelInfoRepository: FileRelInfoRepository,

    private val fileInfoMapper: FileInfoMapper
) {
    private fun getObjectMetaData(
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
//            val requestBody = multipartFile.bytes.toRequestBody("${multipartFile.contentType}; charset=utf-8".toMediaTypeOrNull())

            val key = UUID.randomUUID().toString()
            val fileInfoId = fileInfoRepository.save(FileInfoEntity(key = key, name = originalFilename, path = "")).id
            fileRelInfoRepository.save(FileRelInfoEntity(target = target, targetId = targetId, fileInfoId = fileInfoId))

//            S3Util.putObject("$target/$targetId/$key", requestBody, objectMetaData)
            MinioUtil.putObject("$target/$targetId/$key", multipartFile, objectMetaData)
        }
    }

    fun getFileUrl(target: String, targetId: Long): String? {
        return fileRelInfoRepository.findByTargetAndTargetIdAndDeleteYn(target, targetId, Constant.DEL_N).map { fileRelInfo ->
            fileInfoRepository.findById(fileRelInfo.fileInfoId!!).map { fileInfo ->
//                S3Util.getObjectUrl("$target/$targetId/${fileInfo.key}")
                MinioUtil.getObjectUrl("$target/$targetId/${fileInfo.key}")
            }.orElse(null)
        }.orElse(null)
    }

    fun getFileUrls(target: String, targetId: Long): List<String> {
        return buildList {
            fileRelInfoRepository.findAllByTargetAndTargetIdAndDeleteYn(target, targetId, Constant.DEL_N).ifPresent { fileRelInfos ->
                fileRelInfos.forEach {
                    fileInfoRepository.findById(it.fileInfoId!!).ifPresent { fileInfo ->
//                        add(S3Util.getObjectUrl("$target/$targetId/${fileInfo.key}"))
                        add(MinioUtil.getObjectUrl("$target/$targetId/${fileInfo.key}"))
                    }
                }
            }
        }
    }

    fun getFileInfoOptional(id: Long): Optional<FileInfoEntity> {
        return fileInfoRepository.findById(id)
    }

    fun getFileRelInfoOptional(target: String, targetId: Long): Optional<FileRelInfoEntity> {
        return fileRelInfoRepository.findByTargetAndTargetIdAndDeleteYn(target, targetId, Constant.DEL_N)
    }

    fun getFileInfo(id: Long): FileInfoEntity? {
        return this.getFileInfoOptional(id).getOrNull()
    }

    fun getFileRelInfo(target: String, targetId: Long): FileRelInfoEntity? {
        return this.getFileRelInfoOptional(target, targetId).getOrNull()
    }

    fun getFileCombInfo(target: String, targetId: Long): FileCombInfoDTO? {
        return fileInfoMapper.selectFileCombineInfo(FileRelInfoDTO(target = target, targetId = targetId))
    }

    fun getFileCombInfoCheck(target: String, targetId: Long): FileCombInfoDTO? {
        return checkNotNull(this.getFileCombInfo(target, targetId)) {
            throw BaseException(StatusCode.PROJECT_NOT_FOUND)
        }
    }

    @Transactional
    fun deleteFile(target: String, targetId: Long) {
        val fileRelInfoEntity = this.getFileRelInfoOptional(target, targetId).orElseThrow {
            BaseException(StatusCode.CANNOT_GET_FILE)
        }

        val fileInfoEntity = this.getFileInfoOptional(fileRelInfoEntity.fileInfoId!!).orElseThrow {
            BaseException(StatusCode.CANNOT_GET_FILE)
        }

        fileRelInfoEntity.deleteYn = Constant.DEL_Y
        fileInfoEntity.deleteYn = Constant.DEL_Y

        fileInfoRepository.save(fileInfoEntity)
        fileRelInfoRepository.save(fileRelInfoEntity)
    }
}