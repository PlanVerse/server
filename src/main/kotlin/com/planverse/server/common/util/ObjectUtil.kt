package com.planverse.server.common.util

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectUtil {
    private val objectMapper = jacksonObjectMapper().registerModules(Jdk8Module(), JavaTimeModule())

    fun convertObjectToString(obj: Any?): String {
        val jom = objectMapper
        jom.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        jom.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        jom.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

        return jom.writeValueAsString(obj ?: "")
    }

    fun convertObjectToStringPretty(obj: Any?): String {
        val jom = objectMapper
        jom.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        jom.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        jom.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

        return jom.writerWithDefaultPrettyPrinter().writeValueAsString(obj ?: "")
    }
}