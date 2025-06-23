package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 예약 상태 Enum
 */
enum class ReservationStatusEntity {
    PENDING,  // 임시 예약 상태 (결제 대기)
    PAID,     // 결제 완료 상태
    EXPIRED   // 결제 시간 초과로 만료된 상태
}

/**
 * 예약 엔티티
 * 데이터베이스의 RESERVATIONS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "RESERVATIONS")
class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    val reservationId: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    val seat: SeatEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ReservationStatusEntity = ReservationStatusEntity.PENDING,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime
)
