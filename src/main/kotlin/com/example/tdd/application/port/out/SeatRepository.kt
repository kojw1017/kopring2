package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Seat

/**
 * 좌석 데이터 접근을 위한 아웃바운드 포트
 */
interface SeatRepository {
    fun findByScheduleId(scheduleId: Long): List<Seat>
    fun findById(seatId: Long): Seat?
    fun findByScheduleIdAndSeatNumber(scheduleId: Long, seatNumber: Int): Seat?
    fun save(seat: Seat): Seat
    fun saveAll(seats: List<Seat>): List<Seat>
    fun findAvailableSeats(scheduleId: Long): List<Seat>
}
