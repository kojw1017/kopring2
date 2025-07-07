package com.example.tdd.application.port.`in`

import java.time.LocalDateTime
import java.util.UUID

/**
 * 결제 유스케이스 인터페이스
 */
interface PaymentUseCase {
    /**
     * 결제 처리
     */
    fun processPayment(command: ProcessPaymentCommand): PaymentResponse
}

/**
 * 결제 처리 커맨드 DTO
 */
data class ProcessPaymentCommand(
    val token: String,
    val reservationId: UUID
)

/**
 * 결제 응답 DTO
 */
data class PaymentResponse(
    val paymentId: UUID,
    val userId: UUID,
    val concertDateId: UUID,
    val seatNumber: Int,
    val amount: Long,
    val status: String,
    val completedAt: LocalDateTime?
)
