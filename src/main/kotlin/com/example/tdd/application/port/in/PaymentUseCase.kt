package com.example.tdd.application.port.`in`

/**
 * 결제 관련 인바운드 포트
 */
interface PaymentUseCase {
    /**
     * 예약에 대한 결제를 처리합니다.
     */
    fun processPayment(command: ProcessPaymentCommand): PaymentResponse

    /**
     * 결제 내역을 조회합니다.
     */
    fun getPayment(paymentId: Long): PaymentResponse

    /**
     * 사용자의 결제 내역 목록을 조회합니다.
     */
    fun getUserPayments(userId: String): List<PaymentResponse>
}

/**
 * 결제 처리 명령
 */
data class ProcessPaymentCommand(
    val userId: String,
    val reservationId: Long,
    val token: String
)
