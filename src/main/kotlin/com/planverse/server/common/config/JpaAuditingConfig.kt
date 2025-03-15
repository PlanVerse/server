package com.planverse.server.common.config

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.user.dto.UserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
class JpaAuditingConfig(
    private val auditorAwareConfig: AuditorAwareConfig,
) {
    @Bean
    fun auditorProvider(): AuditorAware<Long> {
        return auditorAwareConfig
    }
}

@Configuration
class AuditorAwareConfig(
    @Value("\${spring.profiles.active}")
    private val activeProfile: String,
) : AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        return if (authentication == null || !authentication.isAuthenticated) {
            throw BaseException(StatusCode.FAIL)
        } else if (authentication.principal.equals("anonymousUser")) {
            Optional.of(Constant.SYSTEM_USER)
        } else if (activeProfile == "local") {
            Optional.of(Constant.SYSTEM_TEST_USER)
        } else {
            Optional.of((authentication.principal as UserInfo).id!!)
        }
    }
}
