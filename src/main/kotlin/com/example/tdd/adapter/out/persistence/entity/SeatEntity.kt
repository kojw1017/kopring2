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
import java.math.BigDecimal

/**
 * 좌석 상태 Enum
 */
enum class SeatStatusEntity {
    AVAILABLE,  // 예약 가능한 상태
    RESERVED,   // 임시 배정된 상태 (5분간 유지)
    SOLD        // 결제 완료되어 판매된 상태
}

/**
 * 좌석 엔티티
 * 데이터베이스의 SEATS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "SEATS")
class SeatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    val seatId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: ScheduleEntity,

    @Column(name = "seat_number", nullable = false)
    val seatNumber: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SeatStatusEntity = SeatStatusEntity.AVAILABLE,

    @Column(name = "price", nullable = false)
    val price: BigDecimal
)
