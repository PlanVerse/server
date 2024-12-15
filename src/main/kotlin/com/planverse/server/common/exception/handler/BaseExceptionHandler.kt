package com.planverse.server.common.exception.handler

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.exception.BaseException
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BaseExceptionHandler {
    /**
     * 미확인 오류 핸들링
     */
    @ExceptionHandler(RuntimeException::class, Exception::class)
    fun handleRuntimeException(ex: RuntimeException?): ResponseEntity<BaseResponse<Map<String, String>>> {
        val statusInfo = StatusCode.FAIL
        return ResponseEntity(BaseResponse.error(status = StatusCode.FAIL), statusInfo.httpStatus)
    }

    /**
     * Spring Validation 핸들링
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        val statusInfo = StatusCode.BAD_REQUEST
        return ResponseEntity(BaseResponse.error(status = StatusCode.FAIL, data = errors), statusInfo.httpStatus)
    }

    /**
     * 로그인 핸들링 - 내부적 설정으로 아이디 혹은 비밀번호 중 무엇이 잘못됬는지 알릴 수 있지만 보안사항으로 인해 하기 예외클래스 사용
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun badCredentialsException(ex: BadCredentialsException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val statusInfo = StatusCode.LOGIN_FAIL
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    /**
     * 전범위적 오류 핸들링
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(be: BaseException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val statusInfo = StatusCode[be.status.code]
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }
}