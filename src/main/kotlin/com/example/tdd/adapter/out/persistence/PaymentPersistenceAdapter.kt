package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.PaymentJpaRepository
import com.example.tdd.adapter.out.persistence.repository.ReservationJpaRepository
import com.example.tdd.application.port.out.PaymentRepository
import com.example.tdd.domain.model.Payment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 결제 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class PaymentPersistenceAdapter(
    private val paymentJpaRepository: PaymentJpaRepository,
    private val reservationJpaRepository: ReservationJpaRepository,
    private val mapper: PersistenceMapper
) : PaymentRepository {

    /**
     * ID로 결제 정보를 조회합니다.
     */
    override fun findById(paymentId: Long): Payment? {
        val paymentEntity = paymentJpaRepository.findById(paymentId).orElse(null) ?: return null
        return mapper.mapToDomainPayment(paymentEntity)
    }

    /**
     * 예약 ID로 결제 정보를 조회합니다.
     */
    override fun findByReservationId(reservationId: Long): Payment? {
        val paymentEntity = paymentJpaRepository.findByReservationReservationId(reservationId) ?: return null
        return mapper.mapToDomainPayment(paymentEntity)
    }

    /**
     * 예약 ID로 결제 존재 여부를 확인합니다.
     */
    override fun existsByReservationId(reservationId: Long): Boolean {
        return paymentJpaRepository.existsByReservationReservationId(reservationId)
    }

    /**
     * 결제 정보를 저장합니다.
     */
    @Transactional
    override fun save(payment: Payment): Payment {
        val reservationEntity = reservationJpaRepository.findById(payment.reservationId)
            .orElseThrow { IllegalArgumentException("예약을 찾을 수 없습니다: ${payment.reservationId}") }

        val paymentEntity = mapper.mapToEntityPayment(payment, reservationEntity)
        val savedEntity = paymentJpaRepository.save(paymentEntity)

        return mapper.mapToDomainPayment(savedEntity)
    }
}