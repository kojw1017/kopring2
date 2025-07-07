package com.example.tdd.application.exception

/**
 * 에러 코드 열거형
 */
enum class ErrorCode(val status: Int, val code: String, val defaultMessage: String) {
    // 4xx - 클라이언트 에러
    RESOURCE_NOT_FOUND(404, "E4040", "Resource not found"),
    INVALID_REQUEST(400, "E4000", "Invalid request"),
    RESOURCE_CONFLICT(409, "E4090", "Resource conflict"),
    ACCESS_DENIED(403, "E4030", "Access denied"),

    // 5xx - 서버 에러
    INTERNAL_SERVER_ERROR(500, "E5000", "Internal server error"),

    // 비즈니스 에러
    QUEUE_ERROR(400, "B4001", "Queue operation failed"),
    RESERVATION_ERROR(400, "B4002", "Reservation operation failed"),
    PAYMENT_ERROR(400, "B4003", "Payment operation failed"),
    INSUFFICIENT_BALANCE(400, "B4004", "Insufficient balance for payment")
}
