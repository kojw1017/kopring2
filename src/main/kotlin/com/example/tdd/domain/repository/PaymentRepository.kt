package com.example.tdd.domain.repository

import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.PaymentStatus
import java.util.UUID

/**
 * 결제 리포지토리 인터페이스
 */
interface PaymentRepository {
    /**
     * ID로 결제 조회
     */
    fun findById(id: UUID): Payment?

    /**
     * 사용자 ID로 결제 목록 조회
     */
    fun findAllByUserId(userId: UUID): List<Payment>

    /**
     * 좌석 ID로 결제 조회
     */
    fun findBySeatId(seatId: UUID): Payment?

    /**
     * 좌석 ID와 상태로 결제 조회
     */
    fun findBySeatIdAndStatus(seatId: UUID, status: PaymentStatus): Payment?

    /**
     * 결제 저장
     */
    fun save(payment: Payment): Payment
}
