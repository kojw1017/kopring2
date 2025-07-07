package com.example.tdd.adapter.out.persistence.entity

import com.example.tdd.domain.model.PaymentStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    val id: UUID,

    val userId: UUID,

    val seatId: UUID,

    val amount: Long,

    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,

    val createdAt: LocalDateTime,

    val completedAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(domain: com.example.tdd.domain.model.Payment): PaymentEntity {
            return PaymentEntity(
                id = domain.id,
                userId = domain.userId,
                seatId = domain.seatId,
                amount = domain.amount,
                status = domain.status,
                createdAt = domain.createdAt,
                completedAt = domain.completedAt
            )
        }
    }

    fun toDomain(): com.example.tdd.domain.model.Payment {
        // 도메인 모델의 create 메서드를 사용한 후 상태를 변경하는 방식으로 변환
        val payment = com.example.tdd.domain.model.Payment.create(
            userId = userId,
            seatId = seatId,
            amount = amount
        )

        return when (status) {
            PaymentStatus.PENDING -> payment
            PaymentStatus.COMPLETED -> payment.complete()
            PaymentStatus.FAILED -> payment.fail()
        }
    }
}
