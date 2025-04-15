package com.planverse.server.common.config.security

import com.planverse.server.common.constant.SystemRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
        jwtAccessDeniedHandler: JwtAccessDeniedHandler
    ): SecurityFilterChain {
        return http
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/auth/sign-out"
                    ).hasAnyAuthority(
                        SystemRole.ROLE_ADMIN.name,
                        SystemRole.ROLE_DEVELOPER.name,
                        SystemRole.ROLE_USER.name
                    )
                    .requestMatchers(
                        "/auth/**",
                        "/actuator/prometheus"
                    ).permitAll()
                    .requestMatchers(
                        "/admin/**"
                    ).hasAnyAuthority(SystemRole.ROLE_ADMIN.name)
                    .requestMatchers(
                        "/actuator/**"
                    ).hasAnyAuthority(
                        SystemRole.ROLE_ADMIN.name,
                        SystemRole.ROLE_DEVELOPER.name
                    )
                    .anyRequest().hasAnyAuthority(
                        SystemRole.ROLE_ADMIN.name,
                        SystemRole.ROLE_DEVELOPER.name,
                        SystemRole.ROLE_USER.name
                    )
            }
            .exceptionHandling {
                it
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedMethods = listOf("GET", "POST", "PUT")
        config.allowedOriginPatterns = listOf("*")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")
        config.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}

