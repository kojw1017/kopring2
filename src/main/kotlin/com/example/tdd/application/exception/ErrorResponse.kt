package com.example.tdd.application.exception

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * 표준화된 에러 응답 구조
 */
data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null,
    val errors: List<ValidationError>? = null
)

/**
 * 필드 유효성 검증 에러
 */
data class ValidationError(
    val field: String,
    val message: String
)
