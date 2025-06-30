package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import java.time.LocalDateTime

/**
 * 예약 데이터 접근을 위한 아웃바운드 포트
 */
interface ReservationRepository {
    fun findById(reservationId: Long): Reservation?
    fun save(reservation: Reservation): Reservation
    fun findByUserId(userId: String): List<Reservation>
    fun findBySeatId(seatId: Long): Reservation?
    fun findExpiredReservations(currentTime: LocalDateTime): List<Reservation>
    fun findByStatus(status: ReservationStatus): List<Reservation>
    fun deleteById(reservationId: Long)
}
