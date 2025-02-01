package com.planverse.server.common.config.security

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.util.ObjectUtil
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
        try {
            // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
            val statusInfo = StatusCode.UNAUTHORIZED

            response.status = statusInfo.httpStatus.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(ObjectUtil.convertObjectToString(BaseResponse.error(status = statusInfo, data = null)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}