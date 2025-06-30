package com.example.tdd.adapter.`in`.web.exception

import com.example.tdd.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

/**
 * 글로벌 예외 처리 클래스
 * 도메인 예외를 HTTP 응답으로 변환합니다.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * 도메인 예외를 HTTP 예외로 변환
     */
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = when (ex) {
            is UserNotFoundException,
            is ReservationNotFoundException,
            is ConcertNotFoundException,
            is ScheduleNotFoundException,
            is SeatNotFoundException -> HttpStatus.NOT_FOUND

            is InvalidRequestException,
            is InsufficientBalanceException -> HttpStatus.BAD_REQUEST

            is SeatAlreadyReservedException,
            is SeatAlreadySoldException -> HttpStatus.CONFLICT

            is ReservationExpiredException -> HttpStatus.GONE

            is InvalidTokenException,
            is TokenExpiredException -> HttpStatus.UNAUTHORIZED

            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "도메인 오류가 발생했습니다.",
            path = request.getDescription(false).removePrefix("uri=")
        )

        return ResponseEntity(errorResponse, status)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "서버 내부 오류가 발생했습니다.",
            path = request.getDescription(false).removePrefix("uri=")
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
