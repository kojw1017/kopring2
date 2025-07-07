package com.example.tdd.domain.model

import java.util.UUID

/**
 * 사용자 도메인 모델
 */
class User private constructor(
    val id: UUID,
    val balance: Long
) {
    companion object {
        fun create(): User {
            return User(
                id = UUID.randomUUID(),
                balance = 0
            )
        }

        fun of(id: UUID, balance: Long): User {
            return User(
                id = id,
                balance = balance
            )
        }
    }

    /**
     * 잔액 충전
     */
    fun chargeBalance(amount: Long): User {
        require(amount > 0) { "충전 금액은 0보다 커야 합니다." }
        return copy(balance = this.balance + amount)
    }

    /**
     * 결제
     */
    fun pay(amount: Long): User {
        require(amount > 0) { "결제 금액은 0보다 커야 합니다." }
        require(this.balance >= amount) { "잔액이 부족합니다." }
        return copy(balance = this.balance - amount)
    }

    private fun copy(
        id: UUID = this.id,
        balance: Long = this.balance
    ): User {
        return User(id, balance)
    }
}
