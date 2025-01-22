package com.planverse.server

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@PostConstruct
fun setTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
}
