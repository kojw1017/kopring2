package com.example.tdd.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("Seat 도메인 모델 테스트")
class SeatTest {

    @Test
    @DisplayName("좌석 생성 성공")
    fun createSeat() {
        // given
        val concertDateId = UUID.randomUUID()
        val seatNumber = 15

        // when
        val seat = Seat.create(concertDateId, seatNumber)

        // then
        assertNotNull(seat.id)
        assertEquals(concertDateId, seat.concertDateId)
        assertEquals(seatNumber, seat.seatNumber)
        assertEquals(SeatStatus.AVAILABLE, seat.status)
        assertNull(seat.reservedBy)
        assertNull(seat.reservedAt)
        assertNull(seat.temporaryReservationExpiresAt)
    }

    @Test
    @DisplayName("좌석 임시 예약 성공")
    fun temporaryReserveSuccess() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)
        val temporaryReservationMinutes = 5

        // when
        val reservedSeat = seat.temporaryReserve(userId, temporaryReservationMinutes)

        // then
        assertEquals(SeatStatus.TEMPORARY_RESERVED, reservedSeat.status)
        assertEquals(userId, reservedSeat.reservedBy)
        assertNotNull(reservedSeat.reservedAt)
        assertNotNull(reservedSeat.temporaryReservationExpiresAt)
        assertTrue(reservedSeat.temporaryReservationExpiresAt!!.isAfter(LocalDateTime.now()))
        assertEquals(SeatStatus.AVAILABLE, seat.status) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("이미 예약된 좌석을 임시 예약 시 예외 발생")
    fun temporaryReserveAlreadyReservedSeat() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)
        val temporaryReservedSeat = seat.temporaryReserve(userId, 5)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            temporaryReservedSeat.temporaryReserve(userId, 5)
        }
        assertEquals("이미 예약된 좌석입니다.", exception.message)
    }

    @Test
    @DisplayName("임시 예약 확정(결제 완료) 성공")
    fun confirmReservationSuccess() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)
        val temporaryReservedSeat = seat.temporaryReserve(userId, 5)

        // when
        val confirmedSeat = temporaryReservedSeat.confirmReservation()

        // then
        assertEquals(SeatStatus.RESERVED, confirmedSeat.status)
        assertEquals(userId, confirmedSeat.reservedBy)
        assertNotNull(confirmedSeat.reservedAt)
        assertNull(confirmedSeat.temporaryReservationExpiresAt) // 확정 후에는 만료 시간 필요 없음
    }

    @Test
    @DisplayName("임시 예약 상태가 아닌 좌석을 확정 시 예외 발생")
    fun confirmReservationNonTemporaryReservedSeat() {
        // given
        val concertDateId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            seat.confirmReservation()
        }
        assertEquals("임시 예약 상태가 아닙니다.", exception.message)
    }

    @Test
    @DisplayName("임시 예약이 만료된 좌석을 확정 시 예외 발생")
    fun confirmReservationExpiredSeat() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)
        // 임시 예약 만료 시간을 -1분으로 설정하여 이미 만료된 상태로 생성
        val temporaryReservedSeat = seat.temporaryReserve(userId, -1)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            temporaryReservedSeat.confirmReservation()
        }
        assertEquals("임시 예약이 만료되었습니다.", exception.message)
    }

    @Test
    @DisplayName("임시 예약 취소 성공")
    fun cancelTemporaryReservationSuccess() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)
        val temporaryReservedSeat = seat.temporaryReserve(userId, 5)

        // when
        val canceledSeat = temporaryReservedSeat.cancelTemporaryReservation()

        // then
        assertEquals(SeatStatus.AVAILABLE, canceledSeat.status)
        assertNull(canceledSeat.reservedBy)
        assertNull(canceledSeat.reservedAt)
        assertNull(canceledSeat.temporaryReservationExpiresAt)
    }

    @Test
    @DisplayName("임시 예약 상태가 아닌 좌석을 취소 시 예외 발생")
    fun cancelTemporaryReservationNonTemporaryReservedSeat() {
        // given
        val concertDateId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            seat.cancelTemporaryReservation()
        }
        assertEquals("임시 예약 상태가 아닙니다.", exception.message)
    }

    @Test
    @DisplayName("임시 예약 만료 확인")
    fun isTemporaryReservationExpired() {
        // given
        val concertDateId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val seat = Seat.create(concertDateId, 15)

        // 만료되지 않은 임시 예약
        val validTemporaryReservedSeat = seat.temporaryReserve(userId, 5)

        // 만료된 임시 예약
        val expiredTemporaryReservedSeat = seat.temporaryReserve(userId, -1)

        // when & then
        assertFalse(validTemporaryReservedSeat.isTemporaryReservationExpired())
        assertTrue(expiredTemporaryReservedSeat.isTemporaryReservationExpired())
    }
}
