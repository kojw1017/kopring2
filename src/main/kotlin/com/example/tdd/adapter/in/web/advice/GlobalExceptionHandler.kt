package com.example.tdd.adapter.`in`.web.advice

import com.example.tdd.application.exception.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 애플리케이션 예외 처리
     */
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Application exception occurred: {}", ex.message, ex)

        val errorResponse = ErrorResponse(
            status = ex.errorCode.status,
            code = ex.errorCode.code,
            message = ex.message,
            path = request.requestURI
        )

        return ResponseEntity.status(ex.errorCode.status).body(errorResponse)
    }

    /**
     * 유효성 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Validation exception occurred: {}", ex.message, ex)

        val validationErrors = ex.bindingResult.fieldErrors.map {
            ValidationError(it.field, it.defaultMessage ?: "Invalid value")
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = ErrorCode.INVALID_REQUEST.code,
            message = "Validation failed",
            path = request.requestURI,
            errors = validationErrors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Constraint violation exception occurred: {}", ex.message, ex)

        val validationErrors = ex.constraintViolations.map {
            ValidationError(it.propertyPath.toString(), it.message)
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = ErrorCode.INVALID_REQUEST.code,
            message = "Constraint violation",
            path = request.requestURI,
            errors = validationErrors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected exception occurred: {}", ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            code = ErrorCode.INTERNAL_SERVER_ERROR.code,
            message = "An unexpected error occurred",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
