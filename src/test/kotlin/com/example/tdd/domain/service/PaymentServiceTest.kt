package com.example.tdd.domain.service

import com.example.tdd.adapter.`in`.web.exception.InsufficientBalanceException
import com.example.tdd.adapter.`in`.web.exception.ReservationExpiredException
import com.example.tdd.domain.TestFixture
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.model.SeatStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime

class PaymentServiceTest {

    private val paymentService = PaymentService()

    @Test
    @DisplayName("사용자 잔액이 충분하면 결제는 성공해야 한다")
    fun `process payment success`() {
        // Given
        val user = TestFixture.createUser(balance = "200000")
        val seat = TestFixture.createSeat(status = SeatStatus.RESERVED, price = "100000")
        val reservation = TestFixture.createReservation(user, seat)
        val paymentId = 1L

        // When
        val payment = paymentService.processPayment(user, reservation, seat, paymentId)

        // Then
        assertAll("결제 성공 후 상태 검증",
            { assertEquals(paymentId, payment.paymentId, "결제 ID 검증") },
            { assertEquals(reservation.reservationId, payment.reservationId, "예약 ID 검증") },
            { assertEquals(seat.price, payment.amount, "결제 금액 검증") },
            { assertEquals(BigDecimal("100000"), user.balance, "사용자 잔액 차감 검증") },
            { assertEquals(SeatStatus.SOLD, seat.status, "좌석 상태 변경 검증") },
            { assertEquals(ReservationStatus.PAID, reservation.status, "예약 상태 변경 검증") }
        )
    }

    @Test
    @DisplayName("사용자 잔액이 부족하면 InsufficientBalanceException 예외가 발생해야 한다")
    fun `process payment fails due to insufficient balance`() {
        // Given
        val user = TestFixture.createUser(balance = "50000") // 잔액 부족
        val seat = TestFixture.createSeat(status = SeatStatus.RESERVED, price = "100000")
        val reservation = TestFixture.createReservation(user, seat)
        val paymentId = 1L

        // When & Then
        assertThrows<InsufficientBalanceException> {
            paymentService.processPayment(user, reservation, seat, paymentId)
        }

        // 상태가 변경되지 않았는지 검증
        assertAll("결제 실패 후 상태 불변 검증",
            { assertEquals(BigDecimal("50000"), user.balance, "사용자 잔액 불변 검증") },
            { assertEquals(SeatStatus.RESERVED, seat.status, "좌석 상태 불변 검증") },
            { assertEquals(ReservationStatus.PENDING, reservation.status, "예약 상태 불변 검증") }
        )
    }

    @Test
    @DisplayName("만료된 예약은 결제할 수 없으며 ReservationExpiredException 예외가 발생해야 한다")
    fun `process payment fails for expired reservation`() {
        // Given
        val user = TestFixture.createUser()
        val seat = TestFixture.createSeat(status = SeatStatus.RESERVED)
        val reservation = TestFixture.createReservation(
            user = user,
            seat = seat,
            status = ReservationStatus.EXPIRED, // 이미 만료된 예약
            expiresAt = LocalDateTime.now().minusMinutes(1)
        )
        val paymentId = 1L

        // When & Then
        assertThrows<ReservationExpiredException> {
            paymentService.processPayment(user, reservation, seat, paymentId)
        }
    }
}
