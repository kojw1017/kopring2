package com.example.tdd.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 결제 도메인 모델
 * 예약에 대한 결제 정보를 관리합니다.
 */
class Payment(
    val paymentId: Long,
    val reservationId: Long,
    val amount: BigDecimal,
    val paymentDate: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(amount > BigDecimal.ZERO) { "결제 금액은 0보다 커야 합니다." }
    }
}
