package com.planverse.server.common.constant

sealed class Constant {
    companion object {
        const val SYSTEM_USER: Long = 0
        const val DEL_Y: String = "Y"
        const val DEL_N: String = "N"
        const val FLAG_Y: String = "Y"
        const val FLAG_N: String = "N"
        const val FLAG_TRUE: Boolean = true
        const val FLAG_FALSE: Boolean = false
    }
}