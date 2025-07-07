package com.example.tdd.adapter.out.persistence.adapter

import com.example.tdd.adapter.out.persistence.entity.PaymentEntity
import com.example.tdd.adapter.out.persistence.repository.PaymentJpaRepository
import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.PaymentStatus
import com.example.tdd.domain.repository.PaymentRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PaymentRepositoryAdapter(
    private val paymentJpaRepository: PaymentJpaRepository
) : PaymentRepository {
    override fun findById(id: UUID): Payment? {
        return paymentJpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByUserId(userId: UUID): List<Payment> {
        return paymentJpaRepository.findAllByUserId(userId)
            .map { it.toDomain() }
    }

    override fun findBySeatId(seatId: UUID): Payment? {
        return paymentJpaRepository.findBySeatId(seatId)?.toDomain()
    }

    override fun findBySeatIdAndStatus(seatId: UUID, status: PaymentStatus): Payment? {
        return paymentJpaRepository.findBySeatIdAndStatus(seatId, status)?.toDomain()
    }

    override fun save(payment: Payment): Payment {
        val paymentEntity = PaymentEntity.fromDomain(payment)
        val savedEntity = paymentJpaRepository.save(paymentEntity)
        return savedEntity.toDomain()
    }
}
