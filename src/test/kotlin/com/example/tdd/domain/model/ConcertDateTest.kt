package com.example.tdd.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

@DisplayName("ConcertDate 도메인 모델 테스트")
class ConcertDateTest {

    @Test
    @DisplayName("콘서트 날짜 생성 성공")
    fun createConcertDate() {
        // given
        val date = LocalDate.now().plusDays(7)
        val name = "BTS World Tour"
        val price = 50000L
        val totalSeats = 50

        // when
        val concertDate = ConcertDate.create(date, name, price, totalSeats)

        // then
        assertNotNull(concertDate.id)
        assertEquals(date, concertDate.date)
        assertEquals(name, concertDate.name)
        assertEquals(price, concertDate.price)
        assertEquals(totalSeats, concertDate.totalSeats)
        assertEquals(totalSeats, concertDate.availableSeats) // 처음에는 모든 좌석이 가용 상태
    }

    @Test
    @DisplayName("좌석 예약 시 가용 좌석 수가 감소함")
    fun reserveSeatSuccess() {
        // given
        val concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "BTS World Tour",
            price = 50000L,
            totalSeats = 50
        )

        // when
        val updatedConcertDate = concertDate.reserveSeat()

        // then
        assertEquals(concertDate.availableSeats - 1, updatedConcertDate.availableSeats)
        assertEquals(concertDate.availableSeats, 50) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("가용 좌석이 없을 경우 예약 시 예외 발생")
    fun reserveSeatWithNoAvailableSeats() {
        // given
        var concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "BTS World Tour",
            price = 50000L,
            totalSeats = 1
        )

        // 좌석을 모두 예약하여 가용 좌석이 없는 상태로 만듬
        concertDate = concertDate.reserveSeat()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            concertDate.reserveSeat()
        }
        assertEquals("예약 가능한 좌석이 없습니다.", exception.message)
    }

    @Test
    @DisplayName("좌석 예약 취소 시 가용 좌석 수가 증가함")
    fun cancelSeatReservationSuccess() {
        // given
        val concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "BTS World Tour",
            price = 50000L,
            totalSeats = 50
        )
        val reservedConcertDate = concertDate.reserveSeat()

        // when
        val canceledConcertDate = reservedConcertDate.cancelSeatReservation()

        // then
        assertEquals(concertDate.availableSeats, canceledConcertDate.availableSeats)
        assertEquals(reservedConcertDate.availableSeats, 49) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("모든 좌석이 가용 상태일 때 취소 시 예외 발생")
    fun cancelSeatReservationWithAllSeatsAvailable() {
        // given
        val concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "BTS World Tour",
            price = 50000L,
            totalSeats = 50
        )

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            concertDate.cancelSeatReservation()
        }
        assertEquals("모든 좌석이 이미 가용 상태입니다.", exception.message)
    }
}
