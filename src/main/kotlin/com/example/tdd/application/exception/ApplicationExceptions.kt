package com.example.tdd.application.exception

/**
 * 애플리케이션 기본 예외 클래스
 */
abstract class ApplicationException(
    val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 리소스를 찾을 수 없는 예외
 */
class ResourceNotFoundException(
    resourceType: String,
    identifier: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.RESOURCE_NOT_FOUND,
    message = "$resourceType with identifier '$identifier' not found",
    cause = cause
)

/**
 * 유효하지 않은 요청 예외
 */
class InvalidRequestException(
    override val message: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.INVALID_REQUEST,
    message = message,
    cause = cause
)

/**
 * 리소스 충돌 예외 (동시성 이슈 등)
 */
class ResourceConflictException(
    override val message: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.RESOURCE_CONFLICT,
    message = message,
    cause = cause
)

/**
 * 권한 없음 예외
 */
class AccessDeniedException(
    override val message: String = "Access denied",
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.ACCESS_DENIED,
    message = message,
    cause = cause
)

/**
 * 대기열 관련 예외
 */
class QueueException(
    override val message: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.QUEUE_ERROR,
    message = message,
    cause = cause
)

/**
 * 예약 관련 예외
 */
class ReservationException(
    override val message: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.RESERVATION_ERROR,
    message = message,
    cause = cause
)

/**
 * 결제 관련 예외
 */
class PaymentException(
    override val message: String,
    cause: Throwable? = null
) : ApplicationException(
    errorCode = ErrorCode.PAYMENT_ERROR,
    message = message,
    cause = cause
)
