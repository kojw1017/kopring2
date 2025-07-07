package com.example.tdd.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * 결제 도메인 모델
 */
class Payment private constructor(
    val id: UUID,
    val userId: UUID,
    val seatId: UUID,
    val amount: Long,
    val status: PaymentStatus,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
) {
    companion object {
        fun create(userId: UUID, seatId: UUID, amount: Long): Payment {
            return Payment(
                id = UUID.randomUUID(),
                userId = userId,
                seatId = seatId,
                amount = amount,
                status = PaymentStatus.PENDING,
                createdAt = LocalDateTime.now(),
                completedAt = null
            )
        }
    }

    /**
     * 결제 완료 처리
     */
    fun complete(): Payment {
        require(status == PaymentStatus.PENDING) { "대기 중인 결제만 완료 처리할 수 있습니다." }

        return copy(
            status = PaymentStatus.COMPLETED,
            completedAt = LocalDateTime.now()
        )
    }

    /**
     * 결제 실패 처리
     */
    fun fail(): Payment {
        require(status == PaymentStatus.PENDING) { "대기 중인 결제만 실패 처리할 수 있습니다." }

        return copy(
            status = PaymentStatus.FAILED,
            completedAt = LocalDateTime.now()
        )
    }

    private fun copy(
        id: UUID = this.id,
        userId: UUID = this.userId,
        seatId: UUID = this.seatId,
        amount: Long = this.amount,
        status: PaymentStatus = this.status,
        createdAt: LocalDateTime = this.createdAt,
        completedAt: LocalDateTime? = this.completedAt
    ): Payment {
        return Payment(id, userId, seatId, amount, status, createdAt, completedAt)
    }
}

/**
 * 결제 상태
 */
enum class PaymentStatus {
    PENDING,    // 결제 대기
    COMPLETED,  // 결제 완료
    FAILED      // 결제 실패
}
