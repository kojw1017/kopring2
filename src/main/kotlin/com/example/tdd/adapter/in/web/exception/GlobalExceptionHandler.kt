package com.example.tdd.adapter.`in`.web.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

/**
 * 글로벌 예외 처리 클래스
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리하고 로깅합니다.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 비즈니스 예외 처리
     * 예상된 예외 상황으로, WARN 레벨로 로깅합니다.
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = when (ex) {
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            is InvalidRequestException, is InsufficientBalanceException, is ReservationExpiredException -> HttpStatus.BAD_REQUEST
            is ConcurrentModificationException -> HttpStatus.CONFLICT
            is TokenException -> HttpStatus.UNAUTHORIZED
            is QueueException -> HttpStatus.SERVICE_UNAVAILABLE
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val path = ex.path ?: request.getDescription(false).substringAfter("uri=")
        
        log.warn("Business Exception Occurred: {} (Status: {}, Path: {})", ex.message, status, path)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = path
        )

        return ResponseEntity(errorResponse, status)
    }

    /**
     * 일반 예외 처리
     * 예상치 못한 모든 예외를 처리하며, ERROR 레벨로 스택 트레이스와 함께 로깅합니다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val path = request.getDescription(false).substringAfter("uri=")
        
        // 중요: 스택 트레이스를 포함하여 어떤 코드에서 예외가 발생했는지 명확히 기록합니다.
        log.error("Unhandled Exception Occurred at '{}'", path, ex)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
            path = path
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
