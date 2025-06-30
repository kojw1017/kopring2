package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 콘서트 일정 엔티티
 * 데이터베이스의 SCHEDULES 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "SCHEDULES")
class ScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    val scheduleId: Long = 0,

    @Column(name = "concert_name", nullable = false, length = 100)
    val concertName: String,

    @Column(name = "concert_date", nullable = false)
    val concertDate: LocalDateTime,

    @Column(name = "venue", nullable = false, length = 100)
    val venue: String = "기본 공연장",

    @Column(name = "max_seats", nullable = false)
    val maxSeats: Int = 50
)
