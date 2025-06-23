package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.SeatEntity
import com.example.tdd.adapter.out.persistence.entity.SeatStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * 좌석 JPA 리포지토리
 */
interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {
    /**
     * 특정 일정의 모든 좌석을 조회합니다.
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.schedule.scheduleId = :scheduleId ORDER BY s.seatNumber ASC")
    fun findAllByScheduleId(scheduleId: Long): List<SeatEntity>

    /**
     * 특정 일정과 좌석 번호로 좌석을 조회합니다.
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.schedule.scheduleId = :scheduleId AND s.seatNumber = :seatNumber")
    fun findByScheduleIdAndSeatNumber(scheduleId: Long, seatNumber: Int): SeatEntity?

    /**
     * 좌석 상태를 업데이트합니다.
     */
    @Modifying
    @Query("UPDATE SeatEntity s SET s.status = :status WHERE s.seatId = :seatId")
    fun updateStatus(seatId: Long, status: SeatStatusEntity): Int
}
