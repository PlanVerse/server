package com.planverse.server.common.exception.handler

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.exception.BaseException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.servlet.resource.NoResourceFoundException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class BaseExceptionHandler {
    /**
     * 미확인 오류 핸들링
     */
    @ExceptionHandler(RuntimeException::class, Exception::class)
    fun handleRuntimeException(ex: RuntimeException?): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex?.cause ?: ex?.message }
        val statusInfo = StatusCode.FAIL
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    /**
     * Spring Validation 핸들링
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.message }
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as? FieldError)?.field ?: return@forEach
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        val statusInfo = StatusCode.BAD_REQUEST
        return ResponseEntity(BaseResponse.error(status = statusInfo, data = errors), statusInfo.httpStatus)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.message }
        val errors = mutableMapOf<String, String>()
        when (val cause = ex.cause) {
            is MissingKotlinParameterException -> {
                errors[cause.parameter.name!!] = "필수값입니다."
            }

            is MismatchedInputException -> {
                errors[cause.path.joinToString { it.fieldName }] = "필수값입니다."
            }
        }
        val statusInfo = StatusCode.REQUIRED_PARAMETER_IS_NULL
        return ResponseEntity(BaseResponse.error(status = statusInfo, data = errors), statusInfo.httpStatus)
    }

    /**
     * 로그인 핸들링 - 내부적 설정으로 아이디 혹은 비밀번호 중 무엇이 잘못됬는지 알릴 수 있지만 보안사항으로 인해 하기 예외클래스 사용
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun badCredentialsException(ex: BadCredentialsException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.cause }
        val statusInfo = StatusCode.LOGIN_FAIL
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    /**
     * API 소스 미존재 오류 핸들링
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun noResourceFoundException(ex: NoResourceFoundException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.message }
        val statusInfo = StatusCode.NOT_EXISTS_REQUEST
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    @ExceptionHandler(MethodNotAllowedException::class)
    fun methodNotAllowedException(ex: MethodNotAllowedException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.message }
        val statusInfo = StatusCode.METHOD_NOT_ALLOW
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.message }
        val statusInfo = StatusCode.METHOD_NOT_SUPPORT
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    /**
     * 파라미터 누락 오류 핸들링
     */
    @ExceptionHandler(MissingServletRequestParameterException::class, MissingServletRequestPartException::class)
    fun missingServletRequestParameterException(ex: Exception): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { ex.cause ?: ex.message }
        val statusInfo = StatusCode.BAD_REQUEST
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }

    /**
     * 전범위적 오류 핸들링
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(be: BaseException): ResponseEntity<BaseResponse<Map<String, String>>> {
        logger.error { be.message }
        val statusInfo = StatusCode[be.status.code]
        return ResponseEntity(BaseResponse.error(status = statusInfo), statusInfo.httpStatus)
    }
}