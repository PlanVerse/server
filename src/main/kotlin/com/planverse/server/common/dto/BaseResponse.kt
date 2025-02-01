package com.planverse.server.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.planverse.server.common.constant.StatusCode

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class BaseResponse<T>(
    var success: Boolean? = false,
    var code: String = StatusCode.SUCCESS.code,
    var message: String = StatusCode.SUCCESS.message,
    var data: T? = null,
) {
    companion object {
        fun <T> success(): BaseResponse<T> {
            return BaseResponse(success = true)
        }

        fun <T> success(data: T): BaseResponse<T> {
            return BaseResponse(success = true, data = data)
        }

        fun <T> error(status: StatusCode): BaseResponse<T> {
            return BaseResponse(code = status.code, message = status.message)
        }

        fun <T> error(status: StatusCode, data: T?): BaseResponse<T> {
            return BaseResponse(code = status.code, message = status.message, data = data)
        }
    }
}