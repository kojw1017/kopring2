package com.example.tdd.application.port.`in`

import java.math.BigDecimal

/**
 * 사용자 잔액 관리를 위한 인바운드 포트
 */
interface UserBalanceUseCase {
    /**
     * 사용자 잔액을 충전합니다.
     *
     * @param command 잔액 충전 명령
     * @return 충전 후 잔액 정보
     */
    fun chargeBalance(command: ChargeBalanceCommand): BalanceResponse

    /**
     * 사용자 잔액을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 현재 잔액 정보
     */
    fun getBalance(userId: String): BalanceResponse
}

/**
 * 잔액 충전 명령 데이터
 */
data class ChargeBalanceCommand(
    val userId: String,
    val amount: BigDecimal
)

/**
 * 잔액 응답 데이터
 */
data class BalanceResponse(
    val userId: String,
    val balance: BigDecimal
)

/**
 * 결제 처리를 위한 인바운드 포트
 */
interface PaymentUseCase {
    /**
     * 예약에 대한 결제를 진행합니다.
     *
     * @param command 결제 처리 명령
     * @return 결제 결과
     */
    fun processPayment(command: PaymentCommand): PaymentResponse
}

/**
 * 결제 처리 명령 데이터
 */
data class PaymentCommand(
    val userId: String,
    val reservationId: Long
)

/**
 * 결제 응답 데이터
 */
data class PaymentResponse(
    val paymentId: Long,
    val reservationId: Long,
    val amount: BigDecimal,
    val status: String
)
