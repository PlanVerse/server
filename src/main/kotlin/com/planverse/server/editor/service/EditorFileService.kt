package com.planverse.server.editor.service

import com.planverse.server.common.annotation.Except
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.MinioUtil
import com.planverse.server.editor.dto.EditorFileResponseDTO
import com.planverse.server.file.service.FileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@Except
@Service
@Transactional(readOnly = true)
class EditorFileService(
    private val fileService: FileService,
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
    fun fileSave(targetId: Long, multipartFile: MultipartFile): EditorFileResponseDTO {
        if (multipartFile.isEmpty) {
            throw BaseException(StatusCode.BAD_REQUEST)
        }

        val key: String = UUID.randomUUID().toString()
        fileService.fileSaveReturnPath(key, Constant.FILE_TARGET_EDITOR, targetId, multipartFile)

        return EditorFileResponseDTO.fromByUrl("${Constant.FILE_TARGET_EDITOR}/${targetId}/${key}")
    }

    fun getFileUrl(targetId: Long, key: String): EditorFileResponseDTO {
        val url = MinioUtil.getObjectUrl("${Constant.FILE_TARGET_EDITOR}/$targetId/${key}")
        return EditorFileResponseDTO.fromByPreview(url)
    }
}