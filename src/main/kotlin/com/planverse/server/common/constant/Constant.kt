package com.planverse.server.common.constant

sealed class Constant {
    companion object {
        const val SYSTEM_USER: Long = 0
        const val SYSTEM_TEST_USER: Long = 0
        const val DEL_Y: String = "Y"
        const val DEL_N: String = "N"
        const val FLAG_Y: String = "Y"
        const val FLAG_N: String = "N"
        const val FLAG_TRUE: Boolean = true
        const val FLAG_FALSE: Boolean = false
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val BLACKLIST_TOKEN_KEY = "blacklist"

        // 단일
        const val FILE_TARGET_TEAM = "team"
        const val FILE_TARGET_PROJECT = "project"

        // 복수
        const val FILE_TARGET_CONTENT = "content"

        // 에디터
        const val FILE_TARGET_EDITOR = "editor"
    }
}