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