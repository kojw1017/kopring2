package com.example.tdd.domain.service

import com.example.tdd.adapter.`in`.web.exception.InsufficientBalanceException
import com.example.tdd.adapter.`in`.web.exception.ReservationExpiredException
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus
import com.example.tdd.domain.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime

class PaymentServiceTest {

    private val paymentService = PaymentService()

    @Test
    fun `결제 처리 성공`() {
        // Given
        val user = User(
            userId = "test-user",
            _balance = BigDecimal("200000")
        )

        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.RESERVED,
            price = BigDecimal("100000")
        )

        val reservation = Reservation(
            reservationId = 1L,
            userId = user.userId,
            seatId = seat.seatId,
            _status = ReservationStatus.PENDING,
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )

        val paymentId = 1L

        // When
        val payment = paymentService.processPayment(user, reservation, seat, paymentId)

        // Then
        assertEquals(paymentId, payment.paymentId)
        assertEquals(reservation.reservationId, payment.reservationId)
        assertEquals(seat.price, payment.amount)
        assertEquals(BigDecimal("100000"), user.balance)
        assertEquals(SeatStatus.SOLD, seat.status)
        assertEquals(ReservationStatus.PAID, reservation.status)
    }

    @Test
    fun `잔액 부족으로 결제 실패`() {
        // Given
        val user = User(
            userId = "test-user",
            _balance = BigDecimal("50000") // 잔액 부족
        )

        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.RESERVED,
            price = BigDecimal("100000")
        )

        val reservation = Reservation(
            reservationId = 1L,
            userId = user.userId,
            seatId = seat.seatId,
            _status = ReservationStatus.PENDING,
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )

        val paymentId = 1L

        // When & Then
        assertThrows<InsufficientBalanceException> {
            paymentService.processPayment(user, reservation, seat, paymentId)
        }

        // 상태가 변경되지 않았는지 검증
        assertEquals(BigDecimal("50000"), user.balance)
        assertEquals(SeatStatus.RESERVED, seat.status)
        assertEquals(ReservationStatus.PENDING, reservation.status)
    }

    @Test
    fun `만료된 예약으로 결제 실패`() {
        // Given
        val user = User(
            userId = "test-user",
            _balance = BigDecimal("200000")
        )

        val seat = Seat(
            seatId = 1L,
            scheduleId = 1L,
            seatNumber = 1,
            _status = SeatStatus.RESERVED,
            price = BigDecimal("100000")
        )

        val reservation = Reservation(
            reservationId = 1L,
            userId = user.userId,
            seatId = seat.seatId,
            _status = ReservationStatus.EXPIRED, // 이미 만료된 예약
            expiresAt = LocalDateTime.now().minusMinutes(1)
        )

        val paymentId = 1L

        // When & Then
        assertThrows<ReservationExpiredException> {
            paymentService.processPayment(user, reservation, seat, paymentId)
        }
    }
}
