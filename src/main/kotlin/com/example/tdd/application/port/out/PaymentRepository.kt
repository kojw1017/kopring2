package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Payment

/**
 * 결제 데이터 접근을 위한 아웃바운드 포트
 */
interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(paymentId: Long): Payment?
    fun findByReservationId(reservationId: Long): Payment?
    fun existsByReservationId(reservationId: Long): Boolean
}
