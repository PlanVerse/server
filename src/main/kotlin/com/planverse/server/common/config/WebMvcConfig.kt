package com.planverse.server.common.config

import com.planverse.server.common.config.security.CustomAuthenticationPrincipalArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val customAuthenticationPrincipalArgumentResolver: CustomAuthenticationPrincipalArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customAuthenticationPrincipalArgumentResolver)
    }
}