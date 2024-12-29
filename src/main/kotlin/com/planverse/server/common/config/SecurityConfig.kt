package com.planverse.server.common.config

import com.planverse.server.common.config.security.JwtAccessDeniedHandler
import com.planverse.server.common.config.security.JwtAuthenticationEntryPoint
import com.planverse.server.common.config.security.JwtAuthenticationFilter
import com.planverse.server.common.config.security.JwtTokenProvider
import com.planverse.server.common.constant.SystemRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
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
            .cors { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/auth/**",
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
            .exceptionHandling(Customizer.withDefaults())
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
}

