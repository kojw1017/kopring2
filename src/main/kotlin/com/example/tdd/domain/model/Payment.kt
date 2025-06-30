package com.example.tdd.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 결제 도메인 모델
 */
data class Payment(
    val paymentId: Long,
    val reservationId: Long,
    val amount: BigDecimal,
    val paymentDate: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(amount > BigDecimal.ZERO) { "결제 금액은 0보다 커야 합니다." }
    }

    /**
     * 결제가 유효한지 확인
     */
    fun isValid(): Boolean {
        return amount > BigDecimal.ZERO && paymentDate.isBefore(LocalDateTime.now().plusMinutes(1))
    }
}
