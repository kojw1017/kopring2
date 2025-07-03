package com.example.tdd.domain.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.domain.TestFixture
import com.example.tdd.domain.model.SeatStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ReservationServiceTest {

    private val reservationService = ReservationService()

    @Test
    @DisplayName("예약 가능한 좌석은 예약에 성공해야 한다")
    fun `reserve seat success`() {
        // Given
        val user = TestFixture.createUser()
        val seat = TestFixture.createSeat(status = SeatStatus.AVAILABLE)
        val reservationId = 1L
        val expirationMinutes = 5

        // When
        val reservation = reservationService.reserveSeat(
            userId = user.userId,
            seat = seat,
            reservationId = reservationId,
            expirationMinutes = expirationMinutes
        )

        // Then
        val now = LocalDateTime.now()
        assertAll("좌석 예약 성공 후 상태 검증",
            { assertEquals(reservationId, reservation.reservationId, "예약 ID 검증") },
            { assertEquals(user.userId, reservation.userId, "사용자 ID 검증") },
            { assertEquals(seat.seatId, reservation.seatId, "좌석 ID 검증") },
            { assertEquals(SeatStatus.RESERVED, seat.status, "좌석 상태 변경 검증") },
            { assertTrue(reservation.expiresAt.isAfter(now.plusMinutes(expirationMinutes.toLong() - 1)), "만료 시간 최소값 검증") },
            { assertTrue(reservation.expiresAt.isBefore(now.plusMinutes(expirationMinutes.toLong()).plusSeconds(1)), "만료 시간 최대값 검증") }
        )
    }

    @Test
    @DisplayName("이미 예약된 좌석은 ConcurrentModificationException 예외가 발생해야 한다")
    fun `reserve seat fails for already reserved seat`() {
        // Given
        val user = TestFixture.createUser()
        val seat = TestFixture.createSeat(status = SeatStatus.RESERVED) // 이미 예약된 상태

        // When & Then
        assertThrows<ConcurrentModificationException> {
            reservationService.reserveSeat(
                userId = user.userId,
                seat = seat,
                reservationId = 1L,
                expirationMinutes = 5
            )
        }
    }

    @Test
    @DisplayName("만료된 예약이 처리되면 좌석은 다시 AVAILABLE 상태가 되어야 한다")
    fun `handle expired reservation success`() {
        // Given
        val seat = TestFixture.createSeat(status = SeatStatus.RESERVED)
        val user = TestFixture.createUser()
        val reservation = TestFixture.createReservation(
            user = user,
            seat = seat,
            expiresAt = LocalDateTime.now().minusMinutes(1) // 만료된 예약
        )

        // When
        reservationService.handleExpiredReservation(reservation, seat)

        // Then
        assertEquals(SeatStatus.AVAILABLE, seat.status, "좌석 상태가 AVAILABLE로 변경되었는지 검증")
    }
}
