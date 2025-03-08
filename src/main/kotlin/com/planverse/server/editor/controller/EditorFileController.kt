package com.planverse.server.editor.controller

import com.planverse.server.editor.service.EditorFileService
import com.planverse.server.user.dto.UserInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/editor")
class EditorFileController(
    private val editorFileService: EditorFileService
) {

    @PostMapping("/upload/{targetId}")
    fun uploadEditorFile(userInfo: UserInfo, @PathVariable(required = true) targetId: Long, @RequestPart("file") multipartFile: MultipartFile): ResponseEntity<Any> {
        val res = editorFileService.fileSave(targetId, multipartFile)
        return ResponseEntity.ok().body(res)
    }

    @GetMapping("/{targetId}/{key}")
    fun fetchEditorFile(@PathVariable(required = true) targetId: Long, @PathVariable(required = true) key: String): ResponseEntity<Any> {
        val res = editorFileService.getFileUrl(targetId, key)
        return ResponseEntity.ok().body(res)
    }
}