package com.example.tdd.domain.model

import com.example.tdd.adapter.`in`.web.exception.InsufficientBalanceException
import com.example.tdd.adapter.`in`.web.exception.InvalidRequestException
import java.math.BigDecimal

/**
 * 사용자 도메인 모델
 * 사용자 ID와 충전 잔액을 관리합니다.
 */
class User(
    val userId: String,
    private var _balance: BigDecimal = BigDecimal.ZERO
) {
    val balance: BigDecimal
        get() = _balance

    /**
     * 잔액을 충전합니다.
     * @param amount 충전할 금액
     * @throws InvalidRequestException 음수 금액을 충전하려는 경우
     */
    fun charge(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidRequestException("충전 금액은 0보다 커야 합니다.")
        }
        _balance = _balance.add(amount)
    }

    /**
     * 결제를 처리합니다.
     * @param amount 결제할 금액
     * @throws InvalidRequestException 음수 금액을 결제하려는 경우
     * @throws InsufficientBalanceException 잔액이 부족한 경우
     */
    fun pay(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidRequestException("결제 금액은 0보다 커야 합니다.")
        }
        if (_balance < amount) {
            throw InsufficientBalanceException("잔액이 부족합니다.")
        }
        _balance = _balance.subtract(amount)
    }
}
