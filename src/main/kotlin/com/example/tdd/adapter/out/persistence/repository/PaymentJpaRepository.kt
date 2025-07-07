package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.PaymentEntity
import com.example.tdd.domain.model.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PaymentJpaRepository : JpaRepository<PaymentEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<PaymentEntity>

    fun findBySeatId(seatId: UUID): PaymentEntity?

    fun findBySeatIdAndStatus(seatId: UUID, status: PaymentStatus): PaymentEntity?
}
