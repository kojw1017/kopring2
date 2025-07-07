package com.example.tdd.application.port.`in`

import java.util.UUID

/**
 * 잔액 관리 유스케이스 인터페이스
 */
interface BalanceManagementUseCase {
    /**
     * 잔액 충전
     */
    fun chargeBalance(command: ChargeBalanceCommand): BalanceResponse

    /**
     * 잔액 조회
     */
    fun getBalance(userId: UUID): BalanceResponse
}

/**
 * 잔액 충전 커맨드 DTO
 */
data class ChargeBalanceCommand(
    val userId: UUID,
    val amount: Long
)

/**
 * 잔액 응답 DTO
 */
data class BalanceResponse(
    val userId: UUID,
    val balance: Long
)
