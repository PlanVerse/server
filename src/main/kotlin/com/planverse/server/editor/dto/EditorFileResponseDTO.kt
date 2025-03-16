package com.planverse.server.editor.dto

data class EditorFileResponseDTO(
    val success: Number? = 1,
    // 업로드한 파일 url
    val file: EditorFileInfoDTO,
    // 캡션
    val caption: String? = null,
    // 테두리 여부
    val withBorder: Boolean? = false,
    // 	배경 여부
    val withBackground: Boolean? = false,
    // 화면 너비 여부
    val stretched: Boolean? = false,
) {
    companion object {
        fun fromByPreview(preview: String): EditorFileResponseDTO {
            return EditorFileResponseDTO(
                file = EditorFileInfoDTO(preview = preview),
            )
        }

        fun fromByUrl(url: String): EditorFileResponseDTO {
            return EditorFileResponseDTO(
                file = EditorFileInfoDTO(url = url),
            )
        }
    }
}

data class EditorFileInfoDTO(
    val url: String? = null,
    val preview: String? = null,
)