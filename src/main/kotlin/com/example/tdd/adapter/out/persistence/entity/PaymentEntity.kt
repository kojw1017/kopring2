package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 결제 엔티티
 * 데이터베이스의 PAYMENTS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "PAYMENTS")
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    val paymentId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    val reservation: ReservationEntity,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Column(name = "payment_date", nullable = false)
    val paymentDate: LocalDateTime = LocalDateTime.now()
)
