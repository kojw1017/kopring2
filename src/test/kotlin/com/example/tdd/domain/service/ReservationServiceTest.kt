package com.example.tdd.domain.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime

class ReservationServiceTest {

    private val reservationService = ReservationService()

    @Test
    fun `좌석 예약 성공`() {
        // Given
        val userId = "test-user"
        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.AVAILABLE,
            price = BigDecimal("100000")
        )
        val reservationId = 1L
        val expirationMinutes = 5

        // When
        val reservation = reservationService.reserveSeat(
            userId = userId,
            seat = seat,
            reservationId = reservationId,
            expirationMinutes = expirationMinutes
        )

        // Then
        assertEquals(reservationId, reservation.reservationId)
        assertEquals(userId, reservation.userId)
        assertEquals(seat.seatId, reservation.seatId)
        assertEquals(SeatStatus.RESERVED, seat.status)
        assertTrue(reservation.expiresAt.isAfter(LocalDateTime.now()))
        assertTrue(reservation.expiresAt.isBefore(LocalDateTime.now().plusMinutes(expirationMinutes.toLong() + 1)))
    }

    @Test
    fun `이미 예약된 좌석 예약 시도 실패`() {
        // Given
        val userId = "test-user"
        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.RESERVED, // 이미 예약된 상태
            price = BigDecimal("100000")
        )
        val reservationId = 1L
        val expirationMinutes = 5

        // When & Then
        assertThrows<ConcurrentModificationException> {
            reservationService.reserveSeat(
                userId = userId,
                seat = seat,
                reservationId = reservationId,
                expirationMinutes = expirationMinutes
            )
        }
    }

    @Test
    fun `만료된 예약 처리 성공`() {
        // Given
        val userId = "test-user"
        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.RESERVED,
            price = BigDecimal("100000")
        )

        // 먼저 예약을 생성
        val reservation = reservationService.reserveSeat(
            userId = userId,
            seat = seat,
            reservationId = 1L,
            expirationMinutes = 5
        )

        // 임의로 만료시간을 과거로 설정하여 만료된 상태로 만듦
        val expiredReservation = reservation.copy(expiresAt = LocalDateTime.now().minusMinutes(1))

        // When
        reservationService.handleExpiredReservation(expiredReservation, seat)

        // Then
        assertEquals(SeatStatus.AVAILABLE, seat.status)
    }
}
