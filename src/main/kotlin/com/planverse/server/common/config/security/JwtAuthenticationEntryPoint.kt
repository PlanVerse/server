package com.planverse.server.common.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        val statusInfo = StatusCode.NO_AUTHORITY
        response.status = statusInfo.httpStatus.value()
        setErrorResponse(response, statusInfo)
    }

    private fun setErrorResponse(
        response: ServletResponse,
        statusInfo: StatusCode,
        data: Any? = null
    ) {
        val objectMapper = ObjectMapper()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse = BaseResponse.error(status = statusInfo, data = data)
        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}