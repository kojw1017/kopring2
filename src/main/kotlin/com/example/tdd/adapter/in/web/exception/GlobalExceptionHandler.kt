package com.example.tdd.adapter.`in`.web.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

/**
 * 글로벌 예외 처리 클래스
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리합니다.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val status = when (ex) {
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            is InvalidRequestException -> HttpStatus.BAD_REQUEST
            is ConcurrentModificationException -> HttpStatus.CONFLICT
            is InsufficientBalanceException -> HttpStatus.BAD_REQUEST
            is TokenException -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = ex.path ?: ""
        )

        return ResponseEntity(errorResponse, status)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "서버 내부 오류가 발생했습니다.",
            path = ""
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

/**
 * 에러 응답 DTO
 */
data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
