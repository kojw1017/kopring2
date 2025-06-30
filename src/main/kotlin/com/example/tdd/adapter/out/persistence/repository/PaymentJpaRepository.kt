package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 결제 JPA 리포지토리
 */
interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
    /**
     * 예약 ID로 결제 정보를 조회합니다.
     */
    fun findByReservationReservationId(reservationId: Long): PaymentEntity?

    /**
     * 예약 ID로 결제 존재 여부를 확인합니다.
     */
    fun existsByReservationReservationId(reservationId: Long): Boolean
}
